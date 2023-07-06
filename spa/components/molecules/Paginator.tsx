import parse from "parse-link-header"
import { useTranslation } from "next-export-i18n"

interface Props {
  links: parse.Links
  updateUrl: (url: string) => void
  amountPages: number
}

export const Paginator = ({ links, updateUrl, amountPages }: Props) => {
  const { t } = useTranslation()

  return (
    <div className="flex text-xl items-center" data-testid="pagination">
      {links.prev ? (
        <button
          suppressHydrationWarning
          className="text-cyan-500 cursor-pointer mr-2"
          onClick={() => links.prev !== undefined && updateUrl(links.prev.url)}
        >
          {t("explore.previous")}
        </button>
      ) : (
        <span suppressHydrationWarning className="text-gray-400 cursor-default mr-2">
          {t("explore.previous")}
        </span>
      )}
      <span className="pr-1">
        {new URL(
          links.self !== undefined ? links.self?.url : "javascript:void(0)",
        ).searchParams.get("page")}
      </span>
      <span suppressHydrationWarning>{`${t("explore.of")} ${amountPages}`}</span>
      {links.next ? (
        <button
          suppressHydrationWarning
          className="text-cyan-500 cursor-pointer ml-2"
          onClick={() => links.next !== undefined && updateUrl(links.next.url)}
        >
          {t("explore.next")}
        </button>
      ) : (
        <span suppressHydrationWarning className=" text-gray-400 cursor-default ml-2">
          {t("explore.next")}
        </span>
      )}
    </div>
  )
}

export default Paginator
