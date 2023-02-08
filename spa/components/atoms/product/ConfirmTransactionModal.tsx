import { useTranslation } from "next-export-i18n"
import { useRef } from "react"
import Swal from "sweetalert2"
import User from "../../../types/User"
import Nft from "../../../types/Nft"

interface Props {
  userPendingBuyOrder: User | undefined
  owner: User | undefined
  product: Nft | undefined
  price: number | undefined
  handleConfirm: (txHash: string) => Promise<any | void>
}

// docs: https://daisyui.com/components/modal
export default function ConfirmTransactionModal({
  userPendingBuyOrder,
  price,
  owner,
  product,
  handleConfirm,
}: Props) {
  const { t } = useTranslation()
  const toggleRef = useRef<HTMLLabelElement>(null)
  const txHashRef = useRef<HTMLInputElement>(null)

  const confirm = async () => {
    try {
      await handleConfirm(txHashRef.current!!.value)
      txHashRef.current!!.value = ""
      // close modal on success
      await Swal.fire({ title: t("product.transactionSuccess"), icon: "success" })
      toggleRef.current?.click()
    } catch (e: any) {
      console.log(e.name, e.message)
      Swal.fire({ title: t("product.transactionFailure"), text: e.message, icon: "error" })
    }
  }

  return (
    <>
      <input type="checkbox" id="my-modal" className="modal-toggle" />
      <label ref={toggleRef} htmlFor="my-modal" className="modal cursor-pointer">
        <div className="modal-box relative w-10/12 max-w-2xl">
          {/* <label htmlFor="my-modal" className="btn btn-sm btn-circle absolute right-2 top-2">✕</label> */}
          <h2 className="text-xl font-bold text-center">
            <span
              suppressHydrationWarning
              className="underline underline-offset-8 decoration-dashed decoration-cyan-600"
            >
              {t("product.transactionDetails")}
            </span>
          </h2>
          <div className="flex flex-col gap-3 mt-3">
            <ModalSection title={t("product.transactionFrom")}>
              <li className="list-disc ml-4 marker:text-cyan-600">
                <span suppressHydrationWarning className="font-bold">{t("product.user")}</span>:{" "}
                {userPendingBuyOrder?.username}
              </li>
              <li className="list-disc ml-4 marker:text-cyan-600">
                <span suppressHydrationWarning className="font-bold">{t("product.wallet")}</span>:{" "}
                {userPendingBuyOrder?.wallet}
              </li>
            </ModalSection>
            <ModalSection title={t("product.transactionTo")}>
              <li className="list-disc ml-4 marker:text-cyan-600">
                <span suppressHydrationWarning className="font-bold">{t("product.user")}</span>: {owner?.username}
              </li>
              <li className="list-disc ml-4 marker:text-cyan-600">
                <span suppressHydrationWarning className="font-bold">{t("product.wallet")}</span>: {owner?.wallet}
              </li>
            </ModalSection>
            <ModalSection title={t("product.transactionNft")}>
              <li className="list-disc ml-4 marker:text-cyan-600">
                <span suppressHydrationWarning className="font-bold">{t("product.name")}</span>: {product?.nftName} #
                {product?.nftId}
              </li>
              <li className="list-disc ml-4 marker:text-cyan-600">
                <span suppressHydrationWarning className="font-bold">{t("product.contract")}</span>: {product?.contractAddr}
              </li>
            </ModalSection>
            <ModalSection title={t("product.transactionValue")}>
              <li className="list-disc ml-4 marker:text-cyan-600">{price} ETH</li>
            </ModalSection>
            <div>
              <p suppressHydrationWarning className="font-bold text-lg">
                {t("product.transactionHash")}
              </p>
              <input ref={txHashRef} className="input input-bordered input-md w-full" />
            </div>
          </div>
          <div className="modal-action">
            <button
              suppressHydrationWarning
              onClick={confirm}
              className="btn normal-case border-none bg-cyan-600 hover:bg-cyan-800"
            >
              {t("product.confirmTransaction")}
            </button>
          </div>
        </div>
      </label>
    </>
  )
}

const ModalSection = ({ title, children }: { title: string; children: React.ReactNode }) => {
  return (
    <div>
      <p suppressHydrationWarning className="font-bold text-lg">
        {title}
      </p>
      <ul>{children}</ul>
    </div>
  )
}
