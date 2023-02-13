export default interface Buyorder {
  amount: number
  offeredBy: URL
  self: URL
  sellorder: URL
  status: string // "NEW" || "PENDING" || "CANCELLED" ?
}
