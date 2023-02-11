export interface FetchError {
  name: string
  message: string
  cause: {
    statusCode: number
    errorCode?: string
    field?: string
    description: string
  }
}
