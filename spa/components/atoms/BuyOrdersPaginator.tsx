import { useTranslation } from "next-export-i18n"
import parse from "parse-link-header"

interface Props {
  amountPages: number
  page: number
  updateUrl: (url: string) => void
  links: parse.Links
}

const BuyOrdersPaginator = ({ amountPages, page, updateUrl, links }: Props) => {
  const { t } = useTranslation()

  return (
    <div className="flex justify-center pt-4">
      {page > 1 ? (
        <>
          <button
            suppressHydrationWarning
            className="text-cyan-700 text-lg hover:underline hover:text-cyan-800"
            onClick={() => links.prev !== undefined && updateUrl(links.prev.url)}
          >
            {t("product.previous")}
          </button>
          <button
            className="text-cyan-700 text-lg px-2 hover:underline hover:text-cyan-800"
            onClick={() => links.first !== undefined && updateUrl(links.first.url)}
          >
            1
          </button>
        </>
      ) : (
        <span suppressHydrationWarning className="text-slate-400 text-lg pr-2">
          {t("product.previous")}
        </span>
      )}
      {page - 1 > 1 && (
        <button
          className="text-cyan-700 text-lg px-2 hover:underline hover:text-cyan-800"
          onClick={() => links.prev !== undefined && updateUrl(links.prev.url)}
        >
          ... {page - 1}
        </button>
      )}
      <span className="text-lg px-2">{page}</span>
      {page + 1 < amountPages && (
        <button
          className="text-cyan-700 text-lg px-2 hover:underline hover:text-cyan-800"
          onClick={() => links.next !== undefined && updateUrl(links.next.url)}
        >
          {page + 1} ...
        </button>
      )}
      {page < amountPages ? (
        <>
          <button
            className="text-cyan-700 text-lg px-2 hover:underline hover:text-cyan-800"
            onClick={() => links.last !== undefined && updateUrl(links.last.url)}
          >
            {amountPages}
          </button>
          <button
            suppressHydrationWarning
            className="text-cyan-700 text-lg hover:underline hover:text-cyan-800"
            onClick={() => links.next !== undefined && updateUrl(links.next.url)}
          >
            {t("product.next")}
          </button>
        </>
      ) : (
        <span className="text-slate-400 text-lg px-2">{t("product.next")}</span>
      )}
    </div>
  )
}

export default BuyOrdersPaginator
