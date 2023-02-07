import { useTranslation } from "next-export-i18n"

interface Props {
  amountPages: number
  page: number
  setPage: (page: number) => void
}

const BuyOrdersPaginator = ({ amountPages, page, setPage }: Props) => {
  const { t } = useTranslation()

  return (
    <div className="flex justify-center pt-4">
      {page > 1 ? (
        <>
          <button
            suppressHydrationWarning
            className="text-cyan-700 text-lg hover:underline hover:text-cyan-800"
            onClick={() => setPage(page - 1)}
          >
            {t("product.previous")}
          </button>
          <button
            className="text-cyan-700 text-lg px-2 hover:underline hover:text-cyan-800"
            onClick={() => setPage(1)}
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
          onClick={() => setPage(page - 1)}
        >
          ... {page - 1}
        </button>
      )}
      <span className="text-lg px-2">{page}</span>
      {page + 1 < amountPages && (
        <button
          className="text-cyan-700 text-lg px-2 hover:underline hover:text-cyan-800"
          onClick={() => setPage(page + 1)}
        >
          {page + 1} ...
        </button>
      )}
      {page < amountPages ? (
        <>
          <button
            className="text-cyan-700 text-lg px-2 hover:underline hover:text-cyan-800"
            onClick={() => setPage(amountPages)}
          >
            {amountPages}
          </button>
          <button
            suppressHydrationWarning
            className="text-cyan-700 text-lg hover:underline hover:text-cyan-800"
            onClick={() => setPage(page + 1)}
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
