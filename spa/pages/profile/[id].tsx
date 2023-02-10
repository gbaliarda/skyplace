import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import Link from "next/link"
import Layout from "../../components/Layout"
import ProfileDescription from "../../components/molecules/profile/ProfileDescription"
import InventoryTab from "../../components/molecules/profile/tabs/InventoryTab"
import SellingTab from "../../components/molecules/profile/tabs/SellingTab"
import FavoritedTab from "../../components/molecules/profile/tabs/FavoritedTab"
import BuyordersTab from "../../components/molecules/profile/tabs/BuyordersTab"
import ReviewsTab from "../../components/molecules/profile/tabs/ReviewsTab"
import HistoryTab from "../../components/molecules/profile/tabs/HistoryTab"
import useSession from "../../hooks/useSession"
import { getResourceUrl } from "../../services/endpoints"

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
  const { id } = router.query as {
    // id is retrieved as string, even if marked as number
    id: string
  }
  let { tab } = router.query as {
    tab: string | undefined
  }

  if (tab === undefined || tab === "") tab = "inventory"

  const { userId: loggedUserId } = useSession()
  const parsedUserId = parseInt(id)

  /* TODO: Throw 404 on this case */
  if (id === undefined) return <h1>Error loading User with id {id}</h1>

  const profilePath = `/profile/${id}`

  const activeTabClasses = "border-b-2 border-cyan-600 text-cyan-600 active pb-3"
  const inactiveTabClasses = "border-transparent hover:text-gray-700 pb-3"

  return (
    <Layout>
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
              const tabImage = `/profile/tabs/${isActive ? "active" : "inactive"}/${value.key}.svg`
              return (
                <li className={tabClasses} key={value.key}>
                  <Link
                    href={{
                      pathname: profilePath,
                      query: {
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

        {tab === "history" && parsedUserId === loggedUserId && <HistoryTab userId={parsedUserId} />}

        {tab === "reviews" && <ReviewsTab userId={parsedUserId} />}
      </div>
    </Layout>
  )
}
