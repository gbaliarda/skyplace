import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import Link from "next/link"
import Layout from "../components/Layout"
import ProfileDescription from "../components/molecules/profile/ProfileDescription"
import InventoryTab from "../components/molecules/profile/tabs/InventoryTab"
import SellingTab from "../components/molecules/profile/tabs/SellingTab"
import FavoritedTab from "../components/molecules/profile/tabs/FavoritedTab"
import BuyordersTab from "../components/molecules/profile/tabs/BuyordersTab"
import ReviewsTab from "../components/molecules/profile/tabs/ReviewsTab"
import HistoryTab from "../components/molecules/profile/tabs/HistoryTab"
import useSession from "../hooks/useSession"
import { getResourceUrl } from "../services/endpoints"
import { useUser } from "../services/users"
import Spinner from "../components/atoms/Spinner"

const TABS = [
  { key: "inventory", isPublic: true },
  { key: "selling", isPublic: true },
  { key: "buyorders", isPublic: false },
  { key: "favorited", isPublic: false },
  { key: "history", isPublic: false },
  { key: "reviews", isPublic: true },
]

export default function Profile() {
  const { t } = useTranslation()
  const router = useRouter()
  const { id } = router.query as { id: string }
  let { tab } = router.query as {
    tab: string | undefined
  }

  const tabOptions = ["inventory", "selling", "favorited", "buyorders", "history", "reviews"]

  if (tab === undefined || !tabOptions.includes(tab)) tab = "inventory"

  const { userId: loggedUserId } = useSession()
  const parsedUserId = parseInt(id)
  const { loading, errors } = useUser(parsedUserId)
  if (!router.isReady || loading)
    return (
      <Layout>
        <div className="flex justify-center items-center h-full">
          <Spinner className="w-12 h-12" />
        </div>
      </Layout>
    )
  if (errors || (router.isReady && Number.isNaN(parsedUserId))) router.push("/404")

  const activeTabClasses = "border-b-2 border-cyan-600 text-cyan-600 active pb-3"
  const inactiveTabClasses = "border-transparent hover:text-gray-700 pb-3"

  return (
    <Layout>
      {!errors && !Number.isNaN(parsedUserId) ? (
        <div className="flex flex-col flex-wrap pb-8 gap-8 mx-10 lg:mx-24 xl:mx-40">
          <ProfileDescription userId={parsedUserId} />

          <div className="flex border-b border-gray-200">
            <ul className="flex flex-wrap flex-grow justify-evenly items-center font-medium text-lg text-center text-gray-500">
              {TABS.map((value) => {
                if (!value.isPublic && parsedUserId !== loggedUserId) {
                  return
                }
                const isActive = tab === value.key
                const tabClasses = isActive ? activeTabClasses : inactiveTabClasses
                const tabImage = `/profile/tabs/${isActive ? "active" : "inactive"}/${
                  value.key
                }.svg`
                return (
                  <li className={tabClasses} key={value.key}>
                    <Link
                      href={{
                        pathname: "/profile",
                        query: {
                          id: router.query.id,
                          tab: value.key,
                        },
                      }}
                    >
                      <a>
                        <div className="flex flex-row cursor-pointer">
                          <img
                            className="h-6 w-6 mr-2"
                            src={getResourceUrl(tabImage)}
                            alt="tab_icon"
                          />
                          {t(`profile.${value.key}`)}
                        </div>
                      </a>
                    </Link>
                  </li>
                )
              })}
            </ul>
          </div>

          {tab === "inventory" && <InventoryTab userId={parsedUserId} />}

          {tab === "selling" && <SellingTab userId={parsedUserId} />}

          {tab === "favorited" && parsedUserId === loggedUserId && (
            <FavoritedTab userId={parsedUserId} />
          )}

          {tab === "buyorders" && parsedUserId === loggedUserId && (
            <BuyordersTab userId={parsedUserId} />
          )}

          {tab === "history" && parsedUserId === loggedUserId && (
            <HistoryTab userId={parsedUserId} />
          )}

          {tab === "reviews" && (
            <ReviewsTab loggedInUser={loggedUserId} userId={parsedUserId} />
          )}
        </div>
      ) : (
        <div className="flex justify-center items-center h-full">
          <Spinner className="w-12 h-12" />
        </div>
      )}
    </Layout>
  )
}
