import { useCallback, useState } from "react"
import Link from "next/link"
import { useTranslation } from "next-export-i18n"
import ReviewItem from "../../../atoms/ReviewItem"
import { useReviews } from "../../../../services/reviews"
import Paginator from "../../Paginator"
import ErrorBox from "../../../atoms/ErrorBox"
import Loader from "../../../atoms/Loader"
import { usePurchases } from "../../../../services/purchases"
import useSession from "../../../../hooks/useSession"

interface Props {
  userId: number
}

export default function ReviewsTab({ userId }: Props) {
  const { t } = useTranslation()
  const [page, setPage] = useState(1)

  const handlePageChange = useCallback(
    (newPage: number) => {
      setPage(newPage)
    },
    [setPage],
  )

  const { userId: loggedInUser, accessToken } = useSession()
  const { reviewsInfo, loading, error, mutate: mutateReviews } = useReviews(userId, page)

  /* Both are used to check if a review can be made */

  const {
    reviewsInfo: reviewsBetweenUsers,
    loading: loadingReviewsBetweenUsers,
    error: errorReviewsBetweenUsers,
    mutate: mutateReviewsBetweenUsers,
  } = useReviews(userId, 1, loggedInUser === null ? undefined : loggedInUser)

  const {
    purchases: purchasesBetweenUsers,
    loading: loadingPurchasesBetweenUsers,
    error: errorPurchasesBetweenUsers,
  } = usePurchases(
    loggedInUser === null ? undefined : loggedInUser,
    1,
    accessToken === null ? undefined : accessToken,
    userId,
  )

  const reloadReviews = () => {
    mutateReviews()
    mutateReviewsBetweenUsers()
  }

  const auxEmpty = ["e", "e", "e", "e"]
  const newReviewPath = `/review/${userId}/create`

  if (error)
    return <ErrorBox errorMessage={t("errors.errorLoadingTab")} retryAction={reloadReviews} />
  if (loading) return <Loader className="grow flex items-center justify-center mb-32" />

  /* TODO: Add error and reload on checking */

  if (loadingReviewsBetweenUsers) return <span>CARGANDO REVIEWS</span>
  if (errorReviewsBetweenUsers) return <span>ERROR REVIEWS</span>

  if (loadingPurchasesBetweenUsers) return <span>CARGANDO PURCHASES</span>
  if (errorPurchasesBetweenUsers) return <span>ERROR PURCHASES</span>

  return (
    <>
      <Paginator
        amountPages={reviewsInfo?.totalPages === undefined ? 0 : reviewsInfo.totalPages}
        handlePageChange={handlePageChange}
      />
      <div className="flex flex-row gap-6">
        {/* Mean score */}
        <div className="border rounded-md h-fit p-5">
          <h2 suppressHydrationWarning className="text-lg">
            {t("profile.ratings")}
          </h2>
          <h3 suppressHydrationWarning className="mb-6 text-gray-400">
            {t("profile.totalReviews", { amount: reviewsInfo?.total })}
          </h3>
          <div className="flex flex-col">
            <div className="flex flex-row items-center w-64 xl:w-80 2xl:w-96">
              <div className="flex flex-col gap-3">
                {/* TODO: CHANGE MAGIC NUMBER */}
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

            {purchasesBetweenUsers?.totalPages !== 0 && reviewsBetweenUsers?.total === 0 && (
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

            {/* TODO: add permission check */}

            {/* <c:if test="${param.canReview == true}">
                          <a href="<c:url value='/review/${param.userId}/create'/>">
                              <button type="button" className="shadow-md px-6 py-2.5 rounded-md transition duration-300 bg-cyan-600 hover:bg-cyan-800 text-white hover:shadow-xl">
                                  <spring:message code="profile.addReview" />
                              </button>
                          </a>
                      </c:if> */}
          </div>

          {reviewsInfo?.total === 0 ? (
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
