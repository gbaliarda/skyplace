import { useTranslation } from "next-export-i18n"
import User from "../../types/User"
import ReviewsInfo from "../../types/Review"

export default function ProfileDescription({
  user,
  userId,
  reviewsInfo,
}: {
  user: User | undefined
  userId: number
  reviewsInfo: ReviewsInfo | undefined
}) {
  const { t } = useTranslation()

  const reviewsPath: string = `/profile/${userId}?tab=reviews`
  return (
    <div className="flex flex-col flex-grow md:flex-row items-center mt-10">
      <img
        className="rounded-full h-40 w-40"
        src="/profile/profile_picture.png"
        alt={t("profile.profileIcon")}
      />
      <div className="flex flex-col mt-5 md:ml-5 md:mt-0 items-start justify-center gap-3">
        <div className="flex flex-row items-center justify-between">
          <div className="flex flex-row items-center">
            <span className="text-4xl font-semibold truncate max-w-[18rem] lg:max-w-[28rem] xl:max-w-[36rem] 2xl:max-w-[44rem]">
              {user?.username}
            </span>
            <button
              id="wallet-address-button"
              className="flex flex-row items-center ml-5 lg:ml-10 px-2 py-1 border rounded-2xl"
              data-tooltip-target="tooltip-copy"
              data-tooltip-placement="bottom"
              type="button"
            >
              <img
                className="w-6 h-6"
                src="/profile/utility_icon.svg"
                alt={t("profile.walletIcon")}
              />
              <span
                className="text-xl ml-1 text-gray-400 font-semibold truncate w-28 lg:w-40 hover:text-gray-600"
                id="walletId"
              >
                {user?.wallet}
              </span>
            </button>
            <div
              id="tooltip-copy"
              role="tooltip"
              className="inline-block absolute invisible z-10 py-2 px-3 text-sm font-medium text-white bg-gray-900 rounded-lg shadow-sm opacity-0 tooltip"
            >
              <span suppressHydrationWarning id="message-copy">
                {t("profile.copy")}
              </span>
              <span suppressHydrationWarning id="message-copied" className="hidden">
                {t("profile.copied")}
              </span>
              <div className="tooltip-arrow" data-popper-arrow />
            </div>
          </div>
        </div>
        <span className="text-lg font-light text-gray-400 break-words w-96 lg:w-[36rem] xl:w-[44rem] 2xl:w-[52rem]">
          {user?.email}
        </span>
        <div className="flex flex-row items-center">
          <img className="h-9 w-9" src="/profile/filled_star.svg" alt={t("profile.starIcon")} />
          <p className="text-lg font-bold text-gray-900 ml-1">{reviewsInfo?.score}</p>
          <span className="w-1.5 h-1.5 mx-2 bg-gray-500 rounded-full dark:bg-gray-400" />
          <a
            suppressHydrationWarning
            href={reviewsPath}
            className="text-lg font-medium text-cyan-600 hover:text-cyan-800 hover:underline"
          >
            {t("profile.reviewAmount", { amount: reviewsInfo?.total })}
          </a>
        </div>
      </div>
    </div>
  )
}
