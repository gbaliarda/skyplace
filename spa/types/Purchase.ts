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

export enum PurchaseStatus {
  Success = "Success",
  Cancelled = "Cancelled",
}
