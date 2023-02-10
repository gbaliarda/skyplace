export interface Review {
  id: number
  self: URL
  score: number
  title: string
  comments: string
  reviewer: URL
  reviewee: URL
}

export interface ReviewStarScore {
  star: number
  score: number
}

export default interface ReviewsInfo {
  score: number
  ratings: Array<ReviewStarScore>
  reviews: Array<Review>
}
