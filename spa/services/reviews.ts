import { useEffect } from "react"
import { fetcher } from "./endpoints"
import ReviewsInfo from "../types/Review"
import usePagination from "../hooks/usePagination"

export type ReviewsURL = {
  baseUrl: string
  reviewer?: number
}

export const useReviews = (url: ReviewsURL | null) => {
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
    const params = new URL(_url.baseUrl).searchParams
    if (params.get("page") === "undefined") return
    const reviewsFilter = _url.reviewer ? `&reviewer=${_url.reviewer}` : ""
    fetchData(`${_url.baseUrl}${reviewsFilter}`)
  }

  useEffect(() => {
    if (url) refetchData(url)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(url)])

  return { reviewsInfo, total, totalPages, links, loading, errors, refetchData }
}

export const deleteReview = (
  revieweeId: number,
  reviewId: number,
  accessToken: string | null | undefined,
) =>
  fetcher(`/users/${revieweeId}/reviews/${reviewId}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${accessToken}` },
  })
