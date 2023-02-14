import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import Link from "next/link"
import Skeleton from "react-loading-skeleton"
import Layout from "../components/Layout"
import { useNft, useRecommendedNfts } from "../services/nfts"
import { useUserUrl } from "../services/users"
import { useImageUrl } from "../services/images"
import { useSellorderUrl, usePendingBuyOrder } from "../services/sellorders"
import OffersContent from "../components/molecules/OffersContent"
import Card from "../components/atoms/Card"
import ProductDropdown from "../components/atoms/product/ProductDropdown"
import ProductFavNft from "../components/atoms/product/ProductFavNft"
import ProductDetails from "../components/atoms/product/ProductDetails"
import ProductDescription from "../components/atoms/product/ProductDescription"
import ProductPendingOfferBox from "../components/atoms/product/ProductPendingOfferBox"
import ProductBuyOfferBox from "../components/atoms/product/ProductBuyOfferBox"
import ErrorBox from "../components/atoms/ErrorBox"
import useSession from "../hooks/useSession"
import { useCryptoPrice } from "../hooks/useCryptoPrice"
import ConfirmTransactionModal from "../components/atoms/product/ConfirmTransactionModal"
import { sendJson, getResourceUrl } from "../services/endpoints"
import { useFavoritedNft } from "../services/favorites"

const Product = () => {
  const { t } = useTranslation()
  const router = useRouter()
  const { id } = router.query as { id: string }
  const { nft, loading: loadingNft, errors: errorsNft, mutate: mutateNft } = useNft(id)
  const {
    user: owner,
    loading: loadingOwner,
    errors: errorsOwner,
    mutate: mutateOwner,
  } = useUserUrl(nft?.owner.toString())
  const {
    sellorder,
    loading: loadingSellorder,
    errors: errorsSellorder,
    mutate: mutateSellorder,
  } = useSellorderUrl(nft?.sellorder?.toString())
  const {
    img,
    loading: loadingImage,
    errors: errorsImage,
    mutate: mutateImage,
  } = useImageUrl(nft?.image?.toString())
  const {
    recommendations,
    loading: loadingRecommendations,
    errors: errorsRecommendations,
  } = useRecommendedNfts(nft?.id)
  const { userId: currentUserId, roles, accessToken } = useSession()
  const {
    favorite,
    loading: loadingFavorites,
    errors: errorsFavorites,
    mutate: mutateFavorites,
  } = useFavoritedNft(currentUserId, nft?.id, accessToken)

  const isAdmin = roles !== null ? roles.includes("Admin") : false

  const { price: priceInUsd } = useCryptoPrice("ethereum")
  const { pendingBuyOrder } = usePendingBuyOrder(sellorder?.id)
  const { user: userPendingBuyOrder } = useUserUrl(pendingBuyOrder?.offeredBy?.toString())

  const imageSrc = img !== undefined ? `data:image/jpg;base64,${img.image.toString()}` : undefined
  const ownerUrl = `/profile?id=${owner?.id}`
  const collectionUrl = `/explore?searchFor=collection&search=${nft?.collection}`

  const reloadContent = async () => {
    mutateNft()
    mutateOwner()
    mutateSellorder()
  }

  const handleConfirmPendingOffer = async (txHash: string) => {
    // do not handle the error here, it will be handled in ConfirmTransactionModal
    await sendJson(
      "POST",
      `/sellorders/${sellorder?.id}/buyorders/${userPendingBuyOrder?.id}`,
      { txHash },
      accessToken!!,
    )
    reloadContent()
  }

  if (router.isReady && Number.isNaN(parseInt(id))) router.push("/404")
  if (errorsNft && errorsNft[0].cause.statusCode === 404) router.push("/404")

  return (
    <Layout>
      <ConfirmTransactionModal
        userPendingBuyOrder={userPendingBuyOrder}
        owner={owner}
        product={nft}
        price={pendingBuyOrder?.amount}
        handleConfirm={handleConfirmPendingOffer}
      />
      <div className="font-body overflow-x-hidden">
        <main className="mt-16 mb-8">
          <section className="flex justify-around">
            <div className="container flex flex-col">
              <div className="flex flex-row ">
                <div className="flex-col w-1/2">
                  {loadingImage || errorsImage || errorsNft ? (
                    <div className="w-[95%] h-1/2 m-auto mb-8">
                      {errorsImage || (errorsNft && errorsNft[0].cause.statusCode !== 404) ? (
                        <div className="flex items-center justify-center h-full">
                          <ErrorBox
                            errorMessage={t("errors.errorLoadingImage")}
                            retryAction={mutateImage}
                          />
                        </div>
                      ) : (
                        <Skeleton className="w-full h-full" />
                      )}
                    </div>
                  ) : (
                    <figure className="mb-8 flex justify-center">
                      <img src={imageSrc} alt="nft" className="rounded-2xl max-h-[550px]" />
                    </figure>
                  )}
                  <OffersContent
                    loadingData={loadingOwner || loadingSellorder}
                    owner={owner}
                    buyOrdersUrl={sellorder?.buyorders.toString()}
                  />
                </div>

                {(errorsNft && errorsNft[0].cause.statusCode !== 404) ||
                errorsOwner ||
                errorsSellorder ? (
                  <div className="flex justify-center md:w-3/5">
                    <ErrorBox
                      errorMessage={t("errors.errorLoadingNft")}
                      retryAction={reloadContent}
                    />
                  </div>
                ) : (
                  <div className="md:w-3/5 md:basis-auto md:pl-8 lg:w-1/2 lg:pl-[3.75rem]">
                    <div className="mb-3 flex items-center justify-between">
                      {loadingNft ||
                      loadingOwner ||
                      (currentUserId !== null && loadingFavorites) ? (
                        <div className="flex w-full justify-between items-center">
                          <Skeleton className="w-64 h-6" />
                          <Skeleton className="w-24 h-12" />
                        </div>
                      ) : (
                        <>
                          <Link href={collectionUrl}>
                            <a
                              suppressHydrationWarning
                              className="mr-2 text-cyan-600 font-bold hover:underline hover:text-cyan-800 max-w-[24rem] truncate cursor-pointer"
                            >
                              {t("product.collection", { collectionName: nft?.collection })}
                            </a>
                          </Link>
                          <div className="flex">
                            {currentUserId !== null && !errorsFavorites && (
                              <ProductFavNft
                                amountFavourites={nft?.favorites}
                                isFaved={favorite === undefined ? false : favorite.length > 0}
                                mutateFavs={mutateFavorites}
                                nftId={nft?.id}
                              />
                            )}
                            {(currentUserId === owner?.id || isAdmin) && !pendingBuyOrder && (
                              <ProductDropdown nft={nft!!} isOwner={currentUserId === owner?.id} />
                            )}
                          </div>
                        </>
                      )}
                    </div>

                    <h1 className="mb-2 text-4xl font-semibold break-words line-clamp-3">
                      {loadingNft ? (
                        <Skeleton className="h-10 w-64" />
                      ) : (
                        <span>
                          {nft?.nftName}#{nft?.nftId}
                        </span>
                      )}
                    </h1>
                    {loadingOwner ? (
                      <Skeleton className="w-48 h-6 pt-2 pb-4" />
                    ) : (
                      <div className="flex items-center justify-start pt-2 pb-4 truncate">
                        <span
                          suppressHydrationWarning
                          className="text-jacarta-400 block text-sm h-max"
                        >
                          {t("product.own")}
                        </span>
                        <Link href={ownerUrl} className="text-accent block">
                          <a className="text-sm font-bold pl-2 text-cyan-600 hover:underline hover:text-cyan-800 cursor-pointer">
                            {owner?.username}
                          </a>
                        </Link>
                      </div>
                    )}
                    <div className="mb-8 flex items-center whitespace-nowrap">
                      {loadingNft || (loadingSellorder && nft?.sellorder !== undefined) ? (
                        <>
                          <Skeleton className="w-10 h-12 mr-2" />
                          <Skeleton className="w-48 h-8" />
                        </>
                      ) : nft?.sellorder !== undefined ? (
                        <>
                          <img
                            className="h-8 -ml-1"
                            src={getResourceUrl("/product/eth.svg")}
                            alt="Eth icon"
                          />
                          <div>
                            <span className="font-bold tracking-tight">{sellorder?.price}</span>
                            <span className="ml-4 text-slate-500 text-base">
                              ~ {priceInUsd} USD
                            </span>
                          </div>
                        </>
                      ) : (
                        <>
                          <img
                            className="h-8 -ml-1"
                            src={getResourceUrl("/product/eth.svg")}
                            alt="Eth icon"
                          />
                          <span
                            suppressHydrationWarning
                            className="text-[1.1rem] font-bold tracking-tight"
                          >
                            {t("product.notForSale")}
                          </span>
                        </>
                      )}
                    </div>

                    <ProductDetails
                      loading={loadingNft || (loadingSellorder && nft?.sellorder !== undefined)}
                      contractAddr={nft?.contractAddr}
                      chain={nft?.chain}
                      tokenId={nft?.nftId}
                      category={sellorder?.category}
                    />

                    <ProductDescription description={nft?.description} />

                    {(currentUserId === owner?.id || isAdmin) && pendingBuyOrder && (
                      <ProductPendingOfferBox pendingBuyOrder={pendingBuyOrder} />
                    )}

                    {nft?.sellorder === undefined &&
                      currentUserId !== null &&
                      currentUserId === owner?.id && (
                        <Link href={`/sell?id=${nft?.id}`}>
                          <a
                            suppressHydrationWarning
                            className="btn normal-case font-medium bg-cyan-600 hover:bg-cyan-800 text-white px-6 rounded-md border-0"
                          >
                            {t("product.sell")}
                          </a>
                        </Link>
                      )}

                    {pendingBuyOrder && (currentUserId === userPendingBuyOrder?.id || isAdmin) && (
                      /* eslint-disable */
                      <label
                        suppressHydrationWarning
                        htmlFor="my-modal"
                        className="btn normal-case font-medium bg-cyan-600 hover:bg-cyan-800 text-white px-6 rounded-md border-0"
                      >
                        {t("product.confirm")}
                      </label>
                      /* eslint-enable */
                    )}

                    {sellorder &&
                      currentUserId &&
                      currentUserId !== owner?.id &&
                      userPendingBuyOrder?.id !== currentUserId && (
                        <ProductBuyOfferBox sellOrder={sellorder} />
                      )}
                  </div>
                )}
              </div>
              {!loadingRecommendations && !errorsRecommendations && (
                <>
                  <span suppressHydrationWarning className="font-medium text-2xl mb-6">
                    {t("product.recommended")}
                  </span>
                  <div className="flex flex-wrap justify-center gap-8 items-start">
                    {recommendations?.map((value) => {
                      return <Card nft={value} key={value.id} />
                    })}
                  </div>
                </>
              )}
            </div>
          </section>
        </main>
      </div>
    </Layout>
  )
}

export default Product
