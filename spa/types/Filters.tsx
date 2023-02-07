import { Category, Chain } from "./Nft"

export enum SearchType {
  Nft = "Nft",
  Collection = "Collection",
}

export enum FilterType {
  STATUS = "status",
  CATEGORY = "category",
  CHAIN = "chain",
  PRICE = "price",
}

export enum SaleStatus {
  ONSALE = "onSale",
  NOTSALE = "notSale",
}

export type SearchFilter = {
  searchFor: SearchType
  search: string
}

export type NftsFilter = {
  owner?: number
  [FilterType.STATUS]?: Array<SaleStatus>
  [FilterType.CATEGORY]?: Array<Category>
  [FilterType.CHAIN]?: Array<Chain>
  minPrice?: number
  maxPrice?: number
}
