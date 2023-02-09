import {
  UserCircleIcon,
  BanknotesIcon,
  ShoppingCartIcon,
  ArrowRightOnRectangleIcon,
} from "@heroicons/react/24/outline"
import { UserIcon, HeartIcon } from "@heroicons/react/24/solid"
import Link from "next/link"
import { useRouter } from "next/router"
import useSession from "../../hooks/useSession"

const NavbarDropdown = () => {
  const router = useRouter()
  const { userId } = useSession()
  const isAuthenticated: boolean = userId !== null

  let userProfileUrl = ""
  let userBuyordersUrl = "?tab=buyorders"
  let userSellordersUrl = "?tab=selling"
  let userFavoritesUrl = "?tab=favorited"

  if (isAuthenticated) {
    userProfileUrl = `/profile/${userId}`
    userBuyordersUrl = userProfileUrl.concat(userBuyordersUrl)
    userSellordersUrl = userProfileUrl.concat(userSellordersUrl)
    userFavoritesUrl = userProfileUrl.concat(userFavoritesUrl)
  }

  const logout = () => {
    localStorage.removeItem("access-token")
    localStorage.removeItem("refresh-token")
    sessionStorage.removeItem("access-token")
    sessionStorage.removeItem("refresh-token")
    router.push("/login")
  }

  return (
    <div className="dropdown flex items-center dropdown-bottom dropdown-end">
      <label tabIndex={0} className="cursor-pointer">
        <UserCircleIcon className="h-8 w-8" />
      </label>
      <ul
        tabIndex={0}
        className="dropdown-content menu mt-3 border border-gray-300 text-sm divide-y divide-gray-300 shadow bg-base-100 rounded-sm"
      >
        {isAuthenticated ? (
          <>
            <li>
              <Link href={userProfileUrl} className="py-2 px-4">
                <a className="text-sm hover:bg-gray-600 hover:text-white transition-none">
                  <UserIcon className="h-4 w-4" />
                  Profile
                </a>
              </Link>
            </li>
            <li>
              <Link href={userBuyordersUrl} className="py-2 px-4">
                <a className="text-sm hover:bg-gray-600 hover:text-white transition-none">
                  <BanknotesIcon className="h-4 w-4" />
                  Bidding
                </a>
              </Link>
            </li>
            <li>
              <Link href={userSellordersUrl} className="py-2 px-4">
                <a className="text-sm hover:bg-gray-600 hover:text-white transition-none">
                  <ShoppingCartIcon className="h-4 w-4" />
                  Selling
                </a>
              </Link>
            </li>
            <li>
              <Link href={userFavoritesUrl} className="py-2 px-4">
                <a className="text-sm hover:bg-gray-600 hover:text-white transition-none">
                  <HeartIcon className="h-4 w-4" />
                  Favorited
                </a>
              </Link>
            </li>
            <li>
              <button
                onClick={logout}
                className="text-sm hover:bg-gray-600 hover:text-white transition-none"
              >
                <ArrowRightOnRectangleIcon className="h-4 w-4" />
                Logout
              </button>
            </li>
          </>
        ) : (
          <>
            <li>
              <Link href="/login" className="py-2 px-4">
                <a className="text-sm hover:bg-gray-600 hover:text-white transition-none whitespace-nowrap	">
                  Log in
                </a>
              </Link>
            </li>
            <li>
              <Link href="/register" className="py-2 px-4">
                <a className="text-sm hover:bg-gray-600 hover:text-white transition-none whitespace-nowrap	">
                  Sign up
                </a>
              </Link>
            </li>
          </>
        )}
      </ul>
    </div>
  )
}

export default NavbarDropdown
