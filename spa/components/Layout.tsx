import { ReactNode } from "react"
import Navbar from "./molecules/Navbar"

interface Props {
  children: ReactNode
}

export default function Layout({ children }: Props) {
  return (
    <>
      <Navbar />
      <div className="h-screen pt-16 lg:pt-20">{children}</div>
    </>
  )
}
