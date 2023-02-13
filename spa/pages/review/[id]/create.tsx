import { useTranslation } from "next-export-i18n"
import { FormEvent } from "react"
import { useRouter } from "next/router"
import Swal from "sweetalert2"
import Layout from "../../../components/Layout"
import useSession from "../../../hooks/useSession"
import { useUser } from "../../../services/users"
import useForm from "../../../hooks/useForm"
import { sendJson, getResourceUrl } from "../../../services/endpoints"
import FormSubmit from "../../../components/atoms/forms/FormSubmit"
import FormType from "../../../components/atoms/forms/FormType"
import FormTextArea from "../../../components/atoms/forms/FormTextArea"

interface FormData {
  score: string
  title: string
  comments: string
}

const INITIAL_DATA: FormData = {
  score: "1",
  title: "",
  comments: "",
}

const STARS = [{ key: "1" }, { key: "2" }, { key: "3" }, { key: "4" }, { key: "5" }]

export default function CreateReview() {
  const { t } = useTranslation()
  const [data, updateFields] = useForm<FormData>(INITIAL_DATA)
  const router = useRouter()
  const { id } = router.query as { id: string }
  const parsedId = parseInt(id)
  const { userId, accessToken } = useSession()

  const { user: reviewee, loading: loadingReviewee, errors: errorsReviewee } = useUser(parsedId)

  if (loadingReviewee) return <span>LOADING REVIEWEE</span>
  if (errorsReviewee) return <span>ERROR REVIEWEE</span>

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (!accessToken) return Swal.fire({ title: t("errors.notLoggedIn"), icon: "error" })

    try {
      await sendJson("POST", `/users/${parsedId}/reviews`, { ...data }, accessToken)
      await Swal.fire({ title: t("reviews.createSuccess"), icon: "success" })
      router.replace(`/profile/${parsedId}?tab=reviews`)
    } catch (errs: any) {
      await Swal.fire({
        title: t("errors.createReview"),
        text: t("errors.invalidField", { field: errs[0].cause.field }),
        icon: "error",
      })
    }
  }

  return (
    <Layout>
      <h1 className="text-3xl text-center mb-12">{t("reviews.create")}</h1>
      <form className="flex flex-col justify-center items-center gap-4" onSubmit={handleSubmit}>
        <div className="flex flex-row gap-12">
          <div className="flex flex-col gap-4">
            <input type="hidden" name="reviewerId" required value={userId === null ? "" : userId} />
            <input type="hidden" name="revieweeId" required value={parsedId} />

            <div className="flex flex-col gap-1">
              <span className="text-slate-600">{t("reviews.userReviewed")}</span>
              <div className="flex flex-row items-center">
                <img
                  className="w-14 h-14 rounded-full"
                  src={getResourceUrl("/profile/profile_picture.png")}
                  alt={t("profile.pictureAlt")}
                />
                <span className="ml-2 text-lg">{reviewee?.username}</span>
              </div>
            </div>

            <label className="flex flex-col gap-1">
              <span className="text-slate-600">{t("reviews.rating")}</span>
              <div className="rating rating-lg">
                {STARS.map((value) => {
                  if (value.key === "1")
                    return (
                      <input
                        type="radio"
                        name="rating-2"
                        className="mask mask-star-2 bg-amber-400 checked:hover:bg-amber-400 checked:bg-amber-400 checked:focus:bg-amber-400 rounded-xl checked:bg-none"
                        defaultChecked
                        onClick={() => updateFields({ score: value.key })}
                      />
                    )
                  return (
                    <input
                      type="radio"
                      name="rating-2"
                      className="mask mask-star-2 bg-amber-400 checked:bg-amber-400 checked:focus:bg-amber-400 rounded-xl checked:bg-none"
                      onClick={() => updateFields({ score: value.key })}
                    />
                  )
                })}
              </div>
            </label>
          </div>

          <div className="flex flex-col gap-4">
            {/* Review title */}
            <FormType
              name={t("reviews.title")}
              value={data.title}
              isRequired={false}
              onChange={(e) => updateFields({ title: e.target.value })}
            />

            {/* Review description */}
            <FormTextArea
              name={t("reviews.description")}
              value={data.comments}
              classes="h-60 max-h-60 w-96 pl-3 sm:text-sm rounded-lg focus:ring-cyan-800 focus:border-cyan-800 text-cyan-700 placeholder:text-slate-400 shadow-sm"
              onChange={(e) => updateFields({ comments: e.target.value })}
            />
          </div>
        </div>
        <FormSubmit
          value={t("reviews.create")}
          classes="px-6 py-4 font-bold w-fit rounded-lg shadow-sm cursor-pointer bg-cyan-100 text-cyan-700 hover:bg-cyan-200"
        />
      </form>
    </Layout>
  )
}
