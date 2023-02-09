import Link from "next/link"
import { useTranslation } from "next-export-i18n"

import SearchBar from "../atoms/SearchBar"
import NavbarDropdown from "../atoms/NavbarDropdown"
import { getResourceUrl } from '../../services/endpoints';

const Navbar = () => {
  const { t } = useTranslation()

  return (
    <nav className="absolute flex items-center w-full justify-around h-16 lg:h-20 shadow-md lg:shadow-none">
      <Link href="/">
        <a className="cursor-pointer flex flex-row flex-start gap-2">
          <img src={getResourceUrl("/logo.svg")} alt="Skyplace" className="w-10 md:w-14" />
          <h1 className="font-semibold text-xl md:text-2xl">Skyplace</h1>
        </a>
      </Link>
      <SearchBar />
      <div className="hidden sm:flex flex-row justify-end items-center gap-12 pr-4 text-lg font-normal">
        <Link href="/explore">
          <a
            suppressHydrationWarning
            className="hover:underline decoration-cyan-500 underline-offset-4 cursor-pointer"
          >
            {t("index.explore")}
          </a>
        </Link>
        <Link href="/create">
          <a
            suppressHydrationWarning
            className="hover:underline decoration-cyan-500 underline-offset-4 cursor-pointer"
          >
            {t("index.create")}
          </a>
        </Link>
        <NavbarDropdown />
      </div>
    </nav>
  )
}

export default Navbar
