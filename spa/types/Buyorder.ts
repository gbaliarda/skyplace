export default interface Buyorder {
  amount: number
  offeredBy: URL
  self: URL
  sellorder: URL
}

export interface BuyOrderApi {
  buyorders: Buyorder[]
  totalPages: number
}
