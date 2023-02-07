import useSWR from "swr"
import { fetcher } from "./endpoints"
import ReviewsInfo from "../types/Review"

export const useReviews = (
  userId: number | undefined,
  page?: number | undefined,
  reviewer?: number | undefined,
) => {
  const {
    data: reviewsInfo,
    error,
    mutate,
  } = useSWR<ReviewsInfo>(
    `/users/${userId}/reviews?page=${page === undefined ? "1" : page}${
      reviewer === undefined ? "" : `&reviewer=${reviewer}`
    }`,
    fetcher,
  )
  const loading = !error && !reviewsInfo
  return { reviewsInfo, loading, error, mutate }
}

export const deleteReview = (revieweeId: number, reviewId: number) =>
  fetcher(`/users/${revieweeId}/reviews/${reviewId}`, { method: "DELETE" })
