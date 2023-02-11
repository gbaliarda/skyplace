import { useState } from "react"
import parse from "parse-link-header"
import { checkStatus } from "../services/endpoints"
import { FetchError } from "../types/FetchError"

const usePagination = <T>(requiresSession: boolean = false) => {
  const [elem, setElem] = useState<T>()
  const [loading, setLoading] = useState(false)
  const [links, setLinks] = useState<parse.Links | null>()
  const [total, setTotal] = useState(0)
  const [totalPages, setTotalPages] = useState(1)
  const [error, setError] = useState<FetchError>()

  const fetchData = (_url: string) => {
    if (!_url || _url === "") return
    let accessToken = localStorage.getItem("access-token")
    if (accessToken === null) accessToken = sessionStorage.getItem("access-token")
    const headers = requiresSession ? { headers: { Authorization: `Bearer ${accessToken}` } } : {}
    setLoading(true)
    fetch(_url, headers)
      .then(checkStatus)
      .then((res) => {
        if (res.status === 204) return
        setLinks(parse(res.headers.get("Link")))
        setTotal(parseInt(res.headers.get("X-Total-Count") ?? "0"))
        setTotalPages(parseInt(res.headers.get("X-Total-Pages") ?? "0"))
        res.json().then(setElem)
      })
      .catch(setError)
      .finally(() => setLoading(false))
  }

  return { elem, loading, links, total, totalPages, error, fetchData }
}

export default usePagination
