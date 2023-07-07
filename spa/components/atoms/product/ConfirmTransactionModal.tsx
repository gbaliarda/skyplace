import { useTranslation } from "next-export-i18n"
import { useRef, useState, MouseEvent } from "react"
import Swal from "sweetalert2"
import { ClipboardIcon } from "@heroicons/react/24/outline"
import { CopyToClipboard } from "react-copy-to-clipboard"
import { useRouter } from "next/router"

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
  const router = useRouter()
  const { t } = useTranslation()
  const toggleRef = useRef<HTMLLabelElement>(null)
  const tooltipFromRef = useRef<HTMLButtonElement>(null)
  const tooltipToRef = useRef<HTMLButtonElement>(null)
  const [txHash, setTxHash] = useState("")

  const changeTooltipText = (e: MouseEvent<HTMLButtonElement>, from = false) => {
    const tooltipRef = from ? tooltipFromRef : tooltipToRef
    tooltipRef.current?.setAttribute("data-tip", t("profile.copied"))
    setTimeout(() => {
      tooltipRef.current?.setAttribute("data-tip", t("profile.copy"))
    }, 2000)
  }

  const confirm = async () => {
    try {
      await handleConfirm(txHash)
      setTxHash("")
      const { isConfirmed } = await Swal.fire({
        title: t("product.transactionSuccess"),
        text: t("product.requestReview"),
        icon: "success",
        showCancelButton: true,
        confirmButtonText: t("product.requestReviewConfirm"),
        cancelButtonText: t("product.requestReviewCancel"),
      })
      if (isConfirmed) router.push(`/review?id=${owner?.id}`)
      // Close modal on success
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
                <span suppressHydrationWarning className="font-bold">
                  {t("product.user")}
                </span>
                : {userPendingBuyOrder?.username}
              </li>
              <li className="list-disc ml-4 marker:text-cyan-600 flex items-center">
                <span suppressHydrationWarning className="font-bold">
                  {t("product.wallet")}
                </span>
                : {userPendingBuyOrder?.wallet}
                <CopyToClipboard text={userPendingBuyOrder?.wallet || ""}>
                  <button
                    ref={tooltipFromRef}
                    className="ml-2 tooltip tooltip-top"
                    suppressHydrationWarning
                    data-tip={t("profile.copy")}
                    data-testid="copy-to-clipboard"
                    onClick={(e) => changeTooltipText(e, true)}
                  >
                    <ClipboardIcon className="h-5 w-5 text-cyan-700" />
                  </button>
                </CopyToClipboard>
              </li>
            </ModalSection>
            <ModalSection title={t("product.transactionTo")}>
              <li className="list-disc ml-4 marker:text-cyan-600">
                <span suppressHydrationWarning className="font-bold">
                  {t("product.user")}
                </span>
                : {owner?.username}
              </li>
              <li className="list-disc ml-4 marker:text-cyan-600 flex items-center">
                <span suppressHydrationWarning className="font-bold">
                  {t("product.wallet")}
                </span>
                : {owner?.wallet}
                <CopyToClipboard text={owner?.wallet || ""}>
                  <button
                    ref={tooltipToRef}
                    className="ml-2 tooltip tooltip-top"
                    suppressHydrationWarning
                    data-tip={t("profile.copy")}
                    data-testid="copy-to-clipboard"
                    onClick={changeTooltipText}
                  >
                    <ClipboardIcon className="h-5 w-5 text-cyan-700" />
                  </button>
                </CopyToClipboard>
              </li>
            </ModalSection>
            <ModalSection title={t("product.transactionNft")}>
              <li className="list-disc ml-4 marker:text-cyan-600">
                <span suppressHydrationWarning className="font-bold">
                  {t("product.name")}
                </span>
                : {product?.nftName} #{product?.nftId}
              </li>
              <li className="list-disc ml-4 marker:text-cyan-600">
                <span suppressHydrationWarning className="font-bold">
                  {t("product.contract")}
                </span>
                : {product?.contractAddr}
              </li>
            </ModalSection>
            <ModalSection title={t("product.transactionValue")}>
              <li className="list-disc ml-4 marker:text-cyan-600">
                {price?.toLocaleString("en-US", {
                  maximumFractionDigits: 20,
                })}{" "}
                ETH
              </li>
            </ModalSection>
            <div>
              <p suppressHydrationWarning className="font-bold text-lg">
                {t("product.transactionHash")}
              </p>
              <input
                value={txHash}
                onChange={(e) => setTxHash(e.target.value)}
                className="input input-bordered input-md w-full"
              />
            </div>
          </div>
          <div className="modal-action">
            <button
              suppressHydrationWarning
              onClick={confirm}
              disabled={!txHash}
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
