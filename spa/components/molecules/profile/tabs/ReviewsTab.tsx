import { useCallback, useState } from "react"
import Link from "next/link"
import { useTranslation } from "next-export-i18n"
import ReviewItem from "../../../atoms/ReviewItem"
import { ReviewsURL, useReviews } from "../../../../services/reviews"
import Paginator from "../../Paginator"
import ErrorBox from "../../../atoms/ErrorBox"
import Loader from "../../../atoms/Loader"
import { usePurchases } from "../../../../services/purchases"
import { api } from "../../../../services/endpoints"

interface Props {
  userId: number
  loggedInUser: number
}

export default function ReviewsTab({ userId, loggedInUser }: Props) {
  const { t } = useTranslation()

  const defaultURL = {
    baseUrl: `${api}/users/${userId}/reviews?page=1`,
  } as ReviewsURL
  const [reviewsUrl, setReviewsUrl] = useState<ReviewsURL>(defaultURL)
  const { reviewsInfo, links, loading, total, totalPages, errors, refetchData } =
    useReviews(reviewsUrl)
  const updateUrl = useCallback(
    (_url: string) => {
      setReviewsUrl({
        ...reviewsUrl,
        baseUrl: _url,
      })
    },
    [reviewsUrl],
  )

  const {
    total: totalReviewsBetweenUsers,
    loading: loadingReviewsBetweenUsers,
    refetchData: refetchDataBetweenUsers,
  } = useReviews({
    baseUrl: `${api}/users/${userId}/reviews?page=1`,
    reviewer: loggedInUser ?? undefined,
  })

  const { totalPages: totalPurchasesPagesBetweenUsers, loading: loadingPurchasesBetweenUsers } =
    usePurchases({
      baseUrl: `${api}/users/${loggedInUser}/purchases?page=1`,
      purchaser: userId ?? undefined,
    })

  const reloadReviews = () => {
    refetchData(defaultURL)
    refetchDataBetweenUsers({
      baseUrl: `${api}/users/${userId}/reviews?page=1`,
      reviewer: loggedInUser ?? undefined,
    })
  }

  const auxEmpty = ["e", "e", "e", "e"]
  const newReviewPath = `/review?id=${userId}`

  if (errors)
    return <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={reloadReviews} />
  if (loading || loadingReviewsBetweenUsers || loadingPurchasesBetweenUsers)
    return <Loader className="grow flex items-center justify-center mb-32" />

  return (
    <>
      {links && <Paginator links={links} updateUrl={updateUrl} amountPages={totalPages} />}
      <div className="flex flex-row gap-6">
        {/* Mean score */}
        <div className="border rounded-md h-fit p-5">
          <h2 suppressHydrationWarning className="text-lg">
            {t("profile.ratings")}
          </h2>
          <h3 suppressHydrationWarning className="mb-6 text-gray-400">
            {t("profile.totalReviews", { amount: total })}
          </h3>
          <div className="flex flex-col">
            <div className="flex flex-row items-center w-64 xl:w-80 2xl:w-96">
              <div className="flex flex-col gap-3">
                {auxEmpty.map((_, i) => (
                  <span
                    suppressHydrationWarning
                    className="text-sm font-medium text-blue-700"
                    key={i}
                  >
                    {t("profile.stars", { amount: 5 - i })}
                  </span>
                ))}
                <span suppressHydrationWarning className="text-sm font-medium text-blue-700">
                  {t("profile.star", { amount: 1 })}
                </span>
              </div>
              <div className="flex flex-col grow gap-3">
                {reviewsInfo?.ratings.map((rating) => (
                  <div
                    className="h-5 mx-4 bg-gray-100 rounded border border-gray-300"
                    key={rating.star}
                  >
                    <div
                      className="h-5 bg-amber-500 rounded"
                      style={{ width: `${rating.score}%`, height: "99%" }}
                    />
                  </div>
                ))}
              </div>
              <div className="flex flex-col gap-3">
                {reviewsInfo?.ratings.map((rating) => (
                  <span className="text-sm font-medium text-blue-700" key={rating.star}>
                    {rating.score}%
                  </span>
                ))}
              </div>
            </div>
          </div>
        </div>
        {/* < !--Detailed reviews-- > */}
        <div className="flex flex-col grow divide-y gap-4 mx-8">
          <div className="flex flex-row items-center justify-between">
            <h2 suppressHydrationWarning className="text-2xl">
              {t("profile.reviews")}
            </h2>

            {totalPurchasesPagesBetweenUsers !== 0 &&
              totalReviewsBetweenUsers === 0 &&
              loggedInUser !== userId && (
                <Link href={newReviewPath}>
                  <a>
                    <button
                      type="button"
                      className="shadow-md px-6 py-2.5 rounded-md transition duration-300 bg-cyan-600 hover:bg-cyan-800 text-white hover:shadow-xl"
                    >
                      {t("profile.addReview")}
                    </button>
                  </a>
                </Link>
              )}
          </div>

          {total === 0 ? (
            <span suppressHydrationWarning className="pt-5 text-center text-xl">
              {t("profile.noReviews")}
            </span>
          ) : (
            <div>
              {reviewsInfo?.reviews.map((review, i) => (
                <ReviewItem
                  key={i}
                  review={review}
                  revieweeId={userId}
                  mutateReviews={reloadReviews}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </>
  )
}
