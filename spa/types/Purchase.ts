import Nft from "./Nft"

export default interface Purchase {
  self: URL
  id: number
  price: number
  buyDate: Date
  txHash: string
  nft: Nft
  buyer: URL
  seller: URL
  status: string
}

export interface PurchaseApi {
  purchases: Purchase[]
  totalPages: number
}

export enum PurchaseStatus {
  Success = "Success",
  Cancelled = "Cancelled",
}
