import { FormEvent, useEffect, useRef } from "react"
import { useTranslation } from "next-export-i18n"
import Swal from "sweetalert2"
import { useRouter } from "next/router"
import FormType from "../components/atoms/forms/FormType"
import Layout from "../components/Layout"
import useForm from "../hooks/useForm"
import FormNumber from "../components/atoms/forms/FormNumber"
import FormSelect from "../components/atoms/forms/FormSelect"
import FormSubmit from "../components/atoms/forms/FormSubmit"
import FormTextArea from "../components/atoms/forms/FormTextArea"
import FormFile from "../components/atoms/forms/FormFile"
import useSession from "../hooks/useSession"
import { fetcher } from "../services/endpoints"

export const CHAINS = [
  "Ethereum",
  "BSC",
  "Polygon",
  "Harmony",
  "Solana",
  "Ronin",
  "Cardano",
  "Tezos",
  "Avalanche",
]

interface FormData {
  name: string
  tokenId: number
  contractAddress: string
  blockchain: string
  collection: string
  description: string
}

const INITIAL_DATA: FormData = {
  name: "",
  tokenId: 0,
  contractAddress: "",
  blockchain: CHAINS[0],
  collection: "",
  description: "",
}

export default function Create() {
  const { t } = useTranslation()
  const [data, updateFields] = useForm<FormData>(INITIAL_DATA)
  const fileInputRef = useRef<HTMLInputElement>(null)
  const router = useRouter()
  const { accessToken } = useSession()

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    // @ts-ignore
    const image = fileInputRef.current.files[0]
    const fd = new FormData()
    fd.append("image", image)
    const body = {
      nftId: data.tokenId.toString(),
      contractAddr: data.contractAddress,
      name: data.name,
      chain: data.blockchain,
      collection: data.collection,
      description: data.description,
    }
    fd.append("model", JSON.stringify(body))

    try {
      await fetcher(
        "/nfts",
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
          body: fd,
        },
        true,
      )
      await Swal.fire({ title: t("create.createSuccess"), icon: "success" })
      // TODO: redirect to NFT page
    } catch (err: any) {
      console.log(err.name, err.message)
      Swal.fire({ title: t("errors.createNft"), text: err.message, icon: "error" })
    }
  }

  useEffect(() => {
    if (accessToken === null) {
      router.replace({
        pathname: "/login",
        query: {
          from: "/create",
        },
      })
    }
  }, [accessToken])

  return (
    <Layout>
      <div className="w-full max-w-5xl mx-auto p-4 grow flex flex-col justify-center py-12">
        <h1 suppressHydrationWarning className="text-3xl text-center">
          {t("create.create")}
        </h1>

        <form className="grid grid-cols-2 gap-8 pt-8" onSubmit={handleSubmit}>
          <FormType
            name={t("create.name")}
            placeholder="Space Ape"
            value={data.name}
            onChange={(e) => updateFields({ name: e.target.value })}
          />

          <FormNumber
            name="Id"
            value={data.tokenId}
            onChange={(e) => updateFields({ tokenId: parseInt(e.target.value) })}
          />

          <FormType
            name={t("create.contract")}
            placeholder="0xabcdef0123456789ABCDEF0123456789abcdef01"
            value={data.contractAddress}
            onChange={(e) => updateFields({ contractAddress: e.target.value })}
          />

          <FormSelect
            name="Blockchain"
            options={CHAINS}
            value={data.blockchain}
            onChange={(e) => updateFields({ blockchain: e.target.value })}
          />

          <FormType
            name={t("create.collection")}
            placeholder="Crypto Apes"
            value={data.collection}
            onChange={(e) => updateFields({ collection: e.target.value })}
          />

          <FormFile name={t("create.image")} ref={fileInputRef} />

          <FormTextArea
            name={t("create.description")}
            placeholder=""
            value={data.description}
            classes="min-h-16 max-h-32 pl-3 sm:text-sm rounded-lg border-slate-300 focus:ring-cyan-800 focus:border-cyan-800 text-cyan-700 placeholder:text-slate-400 shadow-sm"
            onChange={(e) => updateFields({ description: e.target.value })}
          />

          <FormSubmit
            value={t("create.publish")}
            classes="px-1 py-4 col-start-2 font-bold rounded-lg shadow-sm cursor-pointer bg-cyan-100 text-cyan-700 hover:bg-cyan-200 disabled:bg-gray-100 disabled:text-gray-500"
          />
        </form>
      </div>
    </Layout>
  )
}
