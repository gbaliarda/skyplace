import { useState, useEffect } from "react"

export default function useSession() {
  const [userId, setUserId] = useState<number | null>(null)
  const [roles, setRoles] = useState<string[] | null>(null)
  const [user, setUser] = useState<string | null>(null)
  const [accessToken, setAccessToken] = useState<string | null | undefined>(undefined)
  const [refreshToken, setRefreshToken] = useState<string | null>(null)

  useEffect(() => {
    const accToken = localStorage.getItem("access-token") || sessionStorage.getItem("access-token")
    if (accToken !== undefined) {
      setAccessToken(accToken)
      if (accToken) {
        const base64 = accToken.split(".")[1].replace("-", "+").replace("_", "/")
        const decoded = Buffer.from(base64, "base64").toString("utf-8")
        const { user: id, roles: userRoles, sub: email } = JSON.parse(decoded)
        setUserId(id)
        setUser(email)
        setRoles(userRoles)
      }
    }
    const refrToken =
      localStorage.getItem("refresh-token") || sessionStorage.getItem("refresh-token")
    if (refrToken) setRefreshToken(refrToken)
  }, [])

  return { userId, user, roles, accessToken, refreshToken }
}
