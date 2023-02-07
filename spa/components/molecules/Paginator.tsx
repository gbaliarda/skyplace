import { BaseSyntheticEvent, useEffect, useRef, useState } from "react"
import { useTranslation } from "next-export-i18n"

interface Props {
  amountPages: number
  handlePageChange: (page: number) => void
}

const Paginator = ({ amountPages, handlePageChange }: Props) => {
  const { t } = useTranslation()
  const pageRef = useRef<HTMLInputElement>(null)
  const [page, setPage] = useState(amountPages > 0 ? 1 : 0)

  const handleUpdatePage = (event: BaseSyntheticEvent) => {
    event.preventDefault()
    if (pageRef.current === null) return

    const newPageToInt = parseInt(pageRef.current.value, 10)
    if (Number.isNaN(newPageToInt) || newPageToInt < 1 || newPageToInt > amountPages) return

    setPage(newPageToInt)
  }

  useEffect(() => {
    if (pageRef.current !== null) pageRef.current.value = page.toString()
    handlePageChange(page)
  }, [handlePageChange, page])

  return (
    <div className="flex text-xl items-center">
      {page > 1 ? (
        <button
          suppressHydrationWarning
          className="text-cyan-400 cursor-pointer mr-2"
          onClick={() => setPage(page - 1)}
        >
          {t("explore.previous")}
        </button>
      ) : (
        <span suppressHydrationWarning className="text-gray-400 cursor-default mr-2">
          {t("explore.previous")}
        </span>
      )}
      <form onSubmit={handleUpdatePage}>
        <input
          type="number"
          min="1"
          max={amountPages}
          ref={pageRef}
          className="w-10 border-2 border-slate-300 rounded-lg bg-slate-300 px-1 mx-1 py-0.5"
          defaultValue={page}
        />
      </form>
      <span suppressHydrationWarning>{`${t("explore.of")} ${amountPages}`}</span>
      {page < amountPages ? (
        <button
          suppressHydrationWarning
          className="text-cyan-400 cursor-pointer ml-2"
          onClick={() => setPage(page + 1)}
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
