import { useTranslation } from "next-export-i18n"

interface Props {
  status: string | undefined
  handleStatusChange: (status: string) => void
}

export default function BuyorderStatusFilter({ status, handleStatusChange }: Props) {
  const { t } = useTranslation()

  const activeTagClasses: string =
    "cursor-pointer block px-4 py-2 bg-cyan-600 rounded-lg flex w-full text-white w-max"
  const inactiveTagClasses: string =
    "cursor-pointer block px-4 py-2 bg-slate-100 hover:bg-slate-400 hover:text-white rounded-lg flex w-full text-slate-700 border border-slate-400 transition duration-300 hover:shadow-xl w-max"

  const statusOptions = [
    { key: "ALL", i18n: t("buyorders.ALL") },
    { key: "NEW", i18n: t("buyorders.NEW") },
    { key: "PENDING", i18n: t("buyorders.PENDING") },
    { key: "MYSALES", i18n: t("buyorders.MYSALES") },
  ]

  return (
    <div className="flex flex-row gap-2 mb-0">
      {statusOptions.map((value) => {
        const isActive = status === value.key || (status === undefined && value.key === "ALL")
        return (
          <button onClick={() => handleStatusChange(value.key)} key={value.key}>
            <span className={isActive ? activeTagClasses : inactiveTagClasses}>
              {t(value.i18n)}
            </span>
          </button>
        )
      })}
    </div>
  )
}
