import React from "react"
import { useTranslation } from "next-export-i18n"
import Link from "next/link"
import Skeleton from "react-loading-skeleton"
import { CopyToClipboard } from "react-copy-to-clipboard"
import { useUser } from "../../../services/users"
import ErrorBox from "../../atoms/ErrorBox"
import { useReviews } from "../../../services/reviews"
import { getResourceUrl } from '../../../services/endpoints';

interface Props {
  userId: number
}

export default function ProfileDescription({ userId }: Props) {
  const { t } = useTranslation()
  const { user, loading: loadingUser, error: errorUser, mutate: mutateUser } = useUser(userId)
  const {
    reviewsInfo,
    loading: loadingReviews,
    error: errorReviews,
    mutate: mutateReviews,
  } = useReviews(userId)

  const reloadContent = () => {
    mutateUser()
    mutateReviews()
  }

  const walletCopyMessage = t("profile.copy")
  const walletCopiedMessage = t("profile.copied")
  const walletTooltipRef = React.useRef(null as any)

  const changeWalletTooltipText = () => {
    if (walletTooltipRef.current !== null) {
      walletTooltipRef.current.setAttribute("data-tip", walletCopiedMessage)
      setTimeout(() => {
        walletTooltipRef.current.setAttribute("data-tip", walletCopyMessage)
      }, 1000)
    }
  }

  if (errorUser || errorReviews)
    return (
      <div className="mt-10">
        <ErrorBox
          errorMessage={t("errors.errorLoadingProfileDescription")}
          retryAction={reloadContent}
        />
      </div>
    )

  const reviewsPath = `/profile/${user?.id}?tab=reviews`
  return loadingUser || loadingReviews ? (
    <div className="flex mt-10">
      <Skeleton circle className="w-40 h-40" />
      <div className="flex flex-col justify-center ml-5 gap-3">
        <div className="flex gap-8">
          <Skeleton className="w-48 h-10" />
          <Skeleton className="w-48 h-10 rounded-3xl" />
        </div>
        <Skeleton className="w-36 h-6" />
        <div className="flex items-center gap-2">
          <Skeleton circle className="w-10 h-10" />
          <Skeleton className="w-48 h-8" />
        </div>
      </div>
    </div>
  ) : (
    <div className="flex mt-10">
      <img
        className="rounded-full h-40 w-40"
        src={getResourceUrl("/profile/profile_picture.png")}
        alt={t("profile.profileIcon")}
      />
      <div className="flex flex-col mt-5 md:ml-5 md:mt-0 justify-center gap-3">
        <div className="flex gap-5 lg:gap-10">
          <span className="text-4xl font-semibold truncate max-w-[18rem] lg:max-w-[28rem] xl:max-w-[36rem] 2xl:max-w-[44rem]">
            {user?.username}
          </span>
          <CopyToClipboard text={user?.wallet || ""}>
            <div
              ref={walletTooltipRef}
              className="tooltip tooltip-top w-fit"
              data-tip={walletCopyMessage}
            >
              <button
                className="flex flex-row items-center px-2 py-1 border rounded-2xl"
                onClick={changeWalletTooltipText}
              >
                <img
                  className="w-6 h-6"
                  src={getResourceUrl("/profile/utility_icon.svg")}
                  alt={t("profile.walletIcon")}
                />
                <span
                  className="text-xl ml-1 text-gray-400 font-semibold truncate w-28 lg:w-40 hover:text-gray-600"
                  id="walletId"
                >
                  {user?.wallet}
                </span>
              </button>
            </div>
          </CopyToClipboard>
        </div>
        <span className="text-lg font-light text-gray-400 break-words w-96 lg:w-[36rem] xl:w-[44rem] 2xl:w-[52rem]">
          {user?.email}
        </span>
        <div className="flex flex-row items-center">
          <img className="h-9 w-9" src={getResourceUrl("/profile/filled_star.svg")} alt={t("profile.starIcon")} />
          <p className="text-lg font-bold text-gray-900 ml-1">{reviewsInfo?.score}</p>
          <span className="w-1.5 h-1.5 mx-2 bg-gray-500 rounded-full dark:bg-gray-400" />
          <Link href={reviewsPath}>
            <a
              suppressHydrationWarning
              className="text-lg text-cyan-600 hover:text-cyan-800 hover:underline cursor-pointer font-bold"
            >
              {t("profile.reviewAmount", { amount: reviewsInfo?.total })}
            </a>
          </Link>
        </div>
      </div>
    </div>
  )
}
