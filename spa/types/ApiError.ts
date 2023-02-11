export interface ApiSource {
    pointer?: string
}

export default interface ApiError {
    code: string
    title: string
    source?: ApiSource
}

