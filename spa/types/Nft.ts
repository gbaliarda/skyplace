export default interface Nft {
  id: number
  owner: URL
  nftName: string
  collection: string
  contractAddr: string
  image: URL
  nftId: number
  sellorder: URL
  chain: Chain
  description: string
  purchases: URL
  favorites: number
  isDeleted: boolean
  recommendations: URL
}

export enum Category {
  Collectible = "Collectible",
  Utility = "Utility",
  Gaming = "Gaming",
  Sports = "Sports",
  Music = "Music",
  VR = "VR",
  Memes = "Memes",
  Photography = "Photography",
  Miscellaneous = "Miscellaneous",
  Art = "Art",
}

export enum Chain {
  Ethereum = "Ethereum",
  BSC = "BSC",
  Polygon = "Polygon",
  Harmony = "Harmony",
  Solana = "Solana",
  Ronin = "Ronin",
  Cardano = "Cardano",
  Tezos = "Tezos",
  Avalanche = "Avalanche",
}
