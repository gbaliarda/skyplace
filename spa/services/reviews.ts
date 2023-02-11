import { useEffect } from "react"
import { fetcher } from "./endpoints"
import ReviewsInfo from "../types/Review"
import usePagination from "../hooks/usePagination"

export type ReviewsURL = {
  baseUrl: string
  reviewer?: number
}

export const useReviews = (url: ReviewsURL) => {
  const {
    elem: reviewsInfo,
    loading,
    links,
    total,
    totalPages,
    error: errors,
    fetchData,
  } = usePagination<ReviewsInfo>(false)

  const refetchData = (_url: ReviewsURL) => {
    const reviewsFilter = _url.reviewer ? `&reviewer=${_url.reviewer}` : ""
    fetchData(`${_url.baseUrl}${reviewsFilter}`)
  }

  useEffect(() => {
    refetchData(url)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(url)])

  return { reviewsInfo, total, totalPages, links, loading, errors, refetchData }
}

// export const useReviews = (
//   userId: number | undefined,
//   page?: number | undefined,
//   reviewer?: number | undefined,
// ) => {
//   const {
//     data: reviewsInfo,
//     error,
//     mutate,
//   } = useSWR<ReviewsInfo>(
//     `/users/${userId}/reviews?page=${page === undefined ? "1" : page}${
//       reviewer === undefined ? "" : `&reviewer=${reviewer}`
//     }`,
//     fetcher,
//   )
//   const loading = !error && !reviewsInfo
//   return { reviewsInfo, loading, error, mutate }
// }

export const deleteReview = (revieweeId: number, reviewId: number) =>
  fetcher(`/users/${revieweeId}/reviews/${reviewId}`, { method: "DELETE" })
