import { useTranslation } from "next-export-i18n"
import Link from "next/link"
import Swal from "sweetalert2"
import { Review } from "../../types/Review"
import { useUser } from "../../services/users"
import { deleteReview } from "../../services/reviews"
import { getResourceUrl } from "../../services/endpoints"
import useSession from '../../hooks/useSession';

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
  const { userId, roles } = useSession()

  const reviewerId = parseInt(review.reviewer.toString().split("/").slice(-1)[0])
  const reviewerProfileUrl: string = `/profile/${reviewerId}`

  const { user: reviewer, loading: loadingReviewer, errors: errorsReviewer } = useUser(reviewerId)

  if (errorsReviewer) return <h1>Error loading reviewer of review with id {review.id}</h1>
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
            src={getResourceUrl("/profile/profile_picture.png")}
            alt={t("profile.profileIcon")}
          />
          <div className="space-y-1 font-medium">
            <Link href={reviewerProfileUrl}>
              <a className="text-lg text-cyan-600 decoration-current hover:text-cyan-800 hover:underline cursor-pointer">
                {reviewer?.username}
              </a>
            </Link>
          </div>
        </div>
        <div className="flex flex-row">
          {auxFilled.map((value) => (
            <img
              className="h-9 w-9"
              src={getResourceUrl("/profile/filled_star.svg")}
              alt=""
              key={value}
            />
          ))}
          {auxEmpty.map((value) => (
            <img
              className="h-9 w-9"
              src={getResourceUrl("/profile/empty_star.svg")}
              alt=""
              key={value}
            />
          ))}
        </div>
      </div>
      <h3 className="font-semibold text-gray-500 mt-4">{review.title}</h3>
      <p className="mb-2 mt-1 font-light text-gray-500">{review.comments}</p>
      {/* The button to open modal */}
      { (userId === reviewerId || roles?.includes("Admin")) &&
        <div className="flex flex-row items-center justify-end">
          <button
            onClick={handleOpenDeleteModal}
            className="btn normal-case shadow-md px-6 py-2.5 rounded-md transition duration-300 bg-red-500 hover:bg-red-900 text-white hover:shadow-xl cursor-pointer"
            >
            {t("profile.deleteReview")}
          </button>
        </div>
      }
    </div>
  )
}
