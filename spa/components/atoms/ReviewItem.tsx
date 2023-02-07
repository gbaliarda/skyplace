import { useTranslation } from "next-export-i18n"
import Link from "next/link"
import Swal from "sweetalert2"
import { Review } from "../../types/Review"
import { useUser } from "../../services/users"
import { deleteReview } from "../../services/reviews"

export default function ReviewItem({
  review,
  revieweeId,
  mutateReviews,
}: {
  review: Review
  revieweeId: number
  mutateReviews: () => void
}) {
  const { t } = useTranslation()

  const reviewerId = parseInt(review.reviewer.toString().split("/").slice(-1)[0])
  const reviewerProfileUrl: string = `/profile/${reviewerId}`

  const { user: reviewer, loading: loadingReviewer, error: errorReviewer } = useUser(reviewerId)

  if (errorReviewer) return <h1>Error loading reviewer of review with id {review.id}</h1>
  if (loadingReviewer) return <h1>Loading reviewer...</h1>

  const auxFilled = []
  const auxEmpty = ["a", "b", "c", "d", "e"]
  for (let i = 0; i < review.score; i += 1) {
    auxFilled.push(i)
    auxEmpty.pop()
  }

  const handleOpenDeleteModal = async () => {
    Swal.fire({
      title: t("reviews.delete"),
      text: t("reviews.deleteDesc"),
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#0891b2",
      cancelButtonColor: "#ef4444",
      confirmButtonText: t("reviews.confirmDelete"),
      cancelButtonText: t("reviews.cancelDelete"),
    }).then((result) => {
      if (result.isConfirmed) {
        deleteReview(revieweeId, review.id).then(() => mutateReviews())
      }
    })
  }

  return (
    <div className="flex flex-col pt-4">
      <div className="flex flex-row items-center justify-between">
        <div className="flex items-center space-x-4">
          <img
            className="w-14 h-14 rounded-full"
            src="/profile/profile_picture.png"
            alt={t("profile.profileIcon")}
          />
          <div className="space-y-1 font-medium">
            <Link href={reviewerProfileUrl}>
              <span className="text-lg text-cyan-600 decoration-current hover:text-cyan-800 hover:underline cursor-pointer">
                {reviewer?.username}
              </span>
            </Link>
          </div>
        </div>
        <div className="flex flex-row">
          {auxFilled.map((value) => (
            <img className="h-9 w-9" src="/profile/filled_star.svg" alt="" key={value} />
          ))}
          {auxEmpty.map((value) => (
            <img className="h-9 w-9" src="/profile/empty_star.svg" alt="" key={value} />
          ))}
        </div>
      </div>
      <h3 className="font-semibold text-gray-500 mt-4">{review.title}</h3>
      <p className="mb-2 mt-1 font-light text-gray-500">{review.comments}</p>
      {/* The button to open modal */}
      <div className="flex flex-row items-center justify-end">
        <button
          onClick={handleOpenDeleteModal}
          className="btn normal-case shadow-md px-6 py-2.5 rounded-md transition duration-300 bg-red-500 hover:bg-red-900 text-white hover:shadow-xl cursor-pointer"
        >
          {t("profile.deleteReview")}
        </button>
      </div>

      {/* Put this part before </body> tag
      <input type="checkbox" id={deleteModalId} className="modal-toggle" />
      <div className="modal">
        <div className="modal-box relative">
          <label htmlFor={deleteModalId} className="btn btn-sm btn-circle absolute right-2 top-2">
            X
          </label>
          <p>{t("profile:confirmDeleteReview")}</p>
          <button
            onClick={handleDelete}
            className="px-4 py-2 rounded-md bg-red-500 transition duration-300 hover:bg-red-900 text-white shadow-md hover:shadow-xl"
          >
            {t("profile:deleteReview")}
          </button>
        </div>
      </div>
      */}
      {/* TODO: CHECK PERMISSION */}
      {/* <c:if test="${param.isAdmin || param.isReviewer}">
                <c:url value='/review/${param.revieweeId}/delete' var="deletePath" />
                <div className="flex flex-row items-center justify-end">
                    <button id="open-delete-modal-${param.modalId}" className="shadow-md px-6 py-2.5 rounded-md transition duration-300 bg-red-500 hover:bg-red-900 text-white hover:shadow-xl cursor-pointer">
                        <spring:message code="review.delete" />
                    </button>
                </div>
                <!-- Modal -->
                <spring:message code="review.delete" var="deleteReview" />
                <spring:message code="review.deleteDesc" var="deleteReviewDesc" />
                <dialog className="relative p-4 rounded-lg text-center max-w-md" id="delete-modal-${param.modalId}">
                    <button className="absolute top-4 right-6 font-bold text-slate-600" id="close-delete-modal-${param.modalId}">X</button>
                    <h2 className="font-bold text-xl text-red-500"><c:out value="${deleteReview}" /></h2>
                    <p className="py-6 text-slate-600"><c:out value="${deleteReviewDesc}" /></p>
                    <form className="mb-0" action='<c:out value="${deletePath}"/>' method="post">
                        <input type="hidden" name="reviewId" value='<c:out value="${param.reviewId}"/>'>
                            <button className="px-4 py-2 rounded-md text-white bg-red-500 transition duration-300 hover:bg-red-900 text-white shadow-md hover:shadow-xl" type="submit"><spring:message code="deleteModal.delete" /></button>
                    </form>
                </dialog>
            </c:if> */}
    </div>
  )
}
