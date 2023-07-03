import { useRouter } from "next/router"
import Error from "next/error"
import { FormEvent, useEffect, useState } from "react"
import Swal from "sweetalert2"
import { useTranslation } from "next-export-i18n"

import { useSellorderUrl } from "../services/sellorders"
import { useNft } from "../services/nfts"
import Layout from "../components/Layout"
import useForm from "../hooks/useForm"
import FormSelect from "../components/atoms/forms/FormSelect"
import FormNumber from "../components/atoms/forms/FormNumber"
import FormSubmit from "../components/atoms/forms/FormSubmit"
import Navbar from "../components/molecules/Navbar"
import { sendJson, api } from "../services/endpoints"
import useSession from "../hooks/useSession"
import { FetchError } from "../types/FetchError"
import { Category } from "../types/Nft"

const CATEGORIES = Object.keys(Category)

interface FormData {
  category: string
  price: number
}

const INITIAL_DATA: FormData = {
  category: CATEGORIES[0],
  price: 0,
}

interface FieldData {
  name: string
  updateFunction: (error: string) => void
}

interface FieldsDataMap {
  [key: string]: FieldData
}

export default function Sell() {
  const router = useRouter()
  const { update, category, price } = router.query as {
    update?: string
    category?: string
    price?: number
  }
  const { t } = useTranslation()
  const { id } = router.query as { id: string }
  const { nft, errors } = useNft(id)
  const { mutate } = useSellorderUrl(update ? `${api}/sellorders/${update}` : undefined)
  const [data, updateFields] = useForm<FormData>(INITIAL_DATA)
  const { accessToken } = useSession()
  const [loading, setLoading] = useState(false)

  const [categoryError, updateCategoryError] = useState("")
  const [priceError, updatePriceError] = useState("")

  const FIELDS_DATA: FieldsDataMap = {
    category: {
      name: t("sell.category"),
      updateFunction: (error: string) => updateCategoryError(error),
    },
    price: {
      name: t("sell.price"),
      updateFunction: (error: string) => updatePriceError(error),
    },
  }

  useEffect(() => {
    if (router.isReady && !id) router.push("/404")
    if (category) updateFields({ category })
    if (price) updateFields({ price })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [category, price])

  if (errors !== undefined && errors.length > 1)
    return (
      <>
        <Navbar />
        <Error statusCode={errors[0].cause.statusCode} />
      </>
    )

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (!accessToken) return Swal.fire({ title: t("errors.notLoggedIn"), icon: "error" })

    setLoading(true)
    try {
      const method = update ? "PUT" : "POST"
      const endpoint = update ? `/sellorders/${update}` : "/sellorders"
      await sendJson(method, endpoint, { ...data, nftId: id }, accessToken)
      mutate() // update sellorder cached data
      router.replace(`/product?id=${id}`)
    } catch (errs: any) {
      const errorTitle = update ? t("errors.updateSellorder") : t("errors.sellNft")
      let errorFields: string = ""
      let auxIdx = 1
      errs.forEach((err: FetchError) => {
        FIELDS_DATA[err.cause.field === undefined ? "" : err.cause.field].updateFunction(
          err.cause.description,
        )
        errorFields += `${FIELDS_DATA[err.cause.field === undefined ? "" : err.cause.field].name}`

        if (auxIdx < errs.length) errorFields += ", "

        auxIdx += 1
      })
      Swal.fire({
        title: errorTitle,
        text:
          errs.length === 1
            ? t("errors.invalidField", { field: errorFields })
            : t("errors.invalidFields", { fields: errorFields }),
        icon: "error",
      })
    }
    setLoading(false)
  }

  return (
    <Layout>
      <div className="w-full max-w-5xl mx-auto p-4 grow flex flex-col justify-center py-12">
        <h1 suppressHydrationWarning className="text-3xl text-center">
          {t("sell.sellNft")}
        </h1>

        <form onSubmit={handleSubmit} className="grid grid-cols-2 items-end gap-8 pt-16">
          <div className="flex flex-col gap-1">
            <p className="text-slate-600">
              <span suppressHydrationWarning className="font-bold">
                {t("sell.nftId")}:{" "}
              </span>
              {nft?.nftId}
            </p>
          </div>
          <div className="flex flex-col gap-1">
            <p className="text-slate-600">
              <span suppressHydrationWarning className="font-bold">
                {t("sell.nftContract")}:{" "}
              </span>
              {nft?.contractAddr}
            </p>
          </div>

          <FormSelect
            name={t("sell.category")}
            options={CATEGORIES}
            translations={CATEGORIES.map((categ) => t(`categories.${categ}`))}
            value={data.category}
            error={categoryError}
            onChange={(e) => updateFields({ category: e.target.value })}
          />

          <FormNumber
            name={`${t("sell.price")} (ETH)`}
            decimals={18}
            value={data.price}
            error={priceError}
            onChange={(e) => updateFields({ price: parseFloat(e.target.value) })}
          />

          <FormSubmit
            disabled={loading}
            value={update ? t("sell.update") : t("sell.publish")}
            classes="px-1 py-4 col-start-2 font-bold rounded-lg shadow-sm cursor-pointer bg-cyan-100 text-cyan-700 hover:bg-cyan-200 disabled:bg-gray-100 disabled:text-gray-500"
          />
        </form>
      </div>
    </Layout>
  )
}
