import { useRouter } from "next/router"
import Error from "next/error"
import { FormEvent, useState } from "react"
import Swal from "sweetalert2"
import { useTranslation } from "next-export-i18n"
import { useNft } from "../../services/nfts"
import Layout from "../../components/Layout"
import useForm from "../../hooks/useForm"
import FormSelect from "../../components/atoms/forms/FormSelect"
import FormNumber from "../../components/atoms/forms/FormNumber"
import FormSubmit from "../../components/atoms/forms/FormSubmit"
import Navbar from "../../components/molecules/Navbar"
import { postJson } from "../../services/endpoints"
import useSession from "../../hooks/useSession"

const CATEGORIES = [
  "Collectible",
  "Utility",
  "Gaming",
  "Sports",
  "Music",
  "VR",
  "Memes",
  "Photography",
  "Miscellaneous",
  "Art",
]

interface FormData {
  category: string
  price: number
}

const INITIAL_DATA: FormData = {
  category: CATEGORIES[0],
  price: 0,
}

export default function Sell() {
  const router = useRouter()
  const { t } = useTranslation()
  const { id } = router.query as { id: string }
  const { nft, error } = useNft(id)
  const [data, updateFields] = useForm<FormData>(INITIAL_DATA)
  const { accessToken } = useSession()
  const [loading, setLoading] = useState(false)

  if (error)
    return (
      <>
        <Navbar />
        <Error statusCode={error.cause.statusCode} />
      </>
    )

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (!accessToken) return Swal.fire({ title: t("errors.notLoggedIn"), icon: "error" })

    setLoading(true)
    try {
      await postJson("/sellorders", { ...data, nftId: id }, accessToken)
      router.push(`/product/${id}`)
    } catch (err: any) {
      console.log(err.name, err.message)
      Swal.fire({ title: t("errors.sellNft"), text: err.message, icon: "error" })
    }
    setLoading(false)
  }

  return (
    <Layout>
      <div className="w-full max-w-5xl mx-auto p-4 grow flex flex-col justify-center py-12">
        <h1 className="text-3xl text-center">Sell an NFT</h1>

        <form onSubmit={handleSubmit} className="grid grid-cols-2 items-end gap-8 pt-16">
          <div className="flex flex-col gap-1">
            <p className="text-slate-600">
              <span className="font-bold">NFT id:</span>
              {nft?.nftId}
            </p>
          </div>
          <div className="flex flex-col gap-1">
            <p className="text-slate-600">
              <span className="font-bold">NFT contract:</span>
              {nft?.contractAddr}
            </p>
          </div>

          <FormSelect
            name="Category"
            options={CATEGORIES}
            value={data.category}
            onChange={(e) => updateFields({ category: e.target.value })}
          />

          <FormNumber
            name="Price (ETH)"
            decimals={18}
            value={data.price}
            onChange={(e) => updateFields({ price: parseFloat(e.target.value) })}
          />

          <FormSubmit
            disabled={loading}
            value="Publish"
            classes="px-1 py-4 col-start-2 font-bold rounded-lg shadow-sm cursor-pointer bg-cyan-100 text-cyan-700 hover:bg-cyan-200 disabled:bg-gray-100 disabled:text-gray-500"
          />
        </form>
      </div>
    </Layout>
  )
}
