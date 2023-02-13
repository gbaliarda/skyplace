import { useTranslation } from "next-export-i18n"
import Link from "next/link"
import Purchase from "../../types/Purchase"
import { useImageUrl } from "../../services/images"
import { useUserUrl } from "../../services/users"
import { epochToIntlDate } from "../../utils/epochToIntlDate"

export default function HistoryItem({ purchase, userId }: { purchase: Purchase; userId: number }) {
  const { t } = useTranslation()

  const purchaseDate = epochToIntlDate(new Date(purchase.buyDate).getTime() / 1000)

  const {
    img: image,
    loading: loadingImage,
    errors: errorsImage,
  } = useImageUrl(purchase?.nft.image.toString())

  const {
    user: seller,
    loading: loadingSeller,
    errors: errorsSeller,
  } = useUserUrl(purchase.seller.toString())
  const {
    user: buyer,
    loading: loadingBuyer,
    errors: errorsBuyer,
  } = useUserUrl(purchase.buyer.toString())

  if (errorsImage) return <h1>Error loading Image of Nft with id {purchase.nft.id}</h1>
  if (loadingImage) return <h1>Loading Image...</h1>

  if (errorsSeller) return <h1>Error loading seller user with {seller?.id}</h1>
  if (loadingSeller && seller !== undefined) return <h1>Loading sellerUser...</h1>

  if (errorsBuyer) return <h1>Error loading buyer user with {buyer?.id}</h1>
  if (loadingBuyer && buyer !== undefined) return <h1>Loading Buyer User...</h1>

  const sold = userId === seller?.id

  const usernameClasses = "text-cyan-600 hover:text-cyan-800 hover:underline cursor-pointer"

  const productLink = () => {
    if (purchase.nft.id !== undefined) {
      return `/product/${purchase.nft.id}`
    }
    return "javascript:void(0)"
  }

  const linkClasses = () => {
    if (purchase.nft.id !== undefined) {
      return "cursor-pointer"
    }
    return ""
  }

  const imageSrc = `data:image/jpg;base64,${image?.image.toString()}`

  const renderIncons = () => {
    if (purchase.status === "SUCCESS" && sold) {
      return (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="h-7 w-7 text-slate-700"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth="2"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"
          />
        </svg>
      )
    }
    if (purchase.status === "SUCCESS") {
      return (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="h-7 w-7 text-slate-700"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth="2"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          />
        </svg>
      )
    }
    return (
      <svg
        className="w-6 h-6"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth="2"
          d="M6 18L18 6M6 6l12 12"
        />
      </svg>
    )
  }

  return (
    <Link href={productLink()}>
      <div
        className={`border-jacarta-100 rounded-2.5xl relative flex items-center border bg-white p-4 transition-shadow hover:shadow-lg ${linkClasses()}`}
      >
        <figure className="mr-5 self-start">
          <img
            src={imageSrc}
            className="w-[6rem] h-[6rem] rounded-lg aspect-square object-cover border border-gray-300"
            alt="avatar 2"
            loading="lazy"
          />
        </figure>
        <div className="max-w-[34rem]">
          <h3 className="font-display text-jacarta-700 mb-1 text-base flex items-center font-semibold truncate">
            {purchase.nft.nftName} #{purchase.nft.nftId}
            {purchase.nft.id === undefined && (
              <span suppressHydrationWarning className="font-normal text-sm text-red-700 ml-4">
                {t("profile.historyRemoved")}
              </span>
            )}
          </h3>
          <div
            suppressHydrationWarning
            className="flex flex-row gap-1 text-jacarta-500 mb-3 text-sm"
          >
            {purchase.status === "SUCCESS" && sold && (
              <>
                {t("profile.soldTo")}
                <Link href={`/profile/${buyer?.id}`}>
                  <a className={usernameClasses}>{buyer?.username}</a>
                </Link>
              </>
            )}
            {purchase.status === "SUCCESS" && !sold && (
              <>
                {t("profile.boughtFrom")}
                <Link href={`/profile/${seller?.id}`}>
                  <a className={usernameClasses}>{seller?.username}</a>
                </Link>
              </>
            )}
            {purchase.status === "CANCELLED" && !sold && (
              <>
                {t("profile.historyErrorBoughtFrom")}
                <Link href={`/profile/${seller?.id}`}>
                  <a className={usernameClasses}>{seller?.username}</a>
                </Link>
              </>
            )}
            {purchase.status === "CANCELLED" && sold && (
              <>
                {t("profile.historyErrorSoldTo")}
                <Link href={`/profile/${seller?.id}`}>
                  <a className={usernameClasses}>{buyer?.username}</a>
                </Link>
              </>
            )}
            {t("profile.historyFor", { price: purchase.price })}
          </div>
          <span className="text-jacarta-300 block text-xs">{purchaseDate}</span>
        </div>

        <div className="border-jacarta-100 ml-auto rounded-full border p-3">{renderIncons()}</div>
      </div>
    </Link>
  )
}
