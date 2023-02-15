import Link from "next/link"
import { useRouter } from "next/router"
import { FormEvent, useState } from "react"
// @ts-ignore
import { useTranslation } from "next-export-i18n"
import Swal from "sweetalert2"
import Layout from "../components/Layout"
import { CHAINS } from "./create"
import FormType from "../components/atoms/forms/FormType"
import useForm from "../hooks/useForm"
import FormSelect from "../components/atoms/forms/FormSelect"
import { createUser, loginUser } from "../services/users"
import { getResourceUrl } from "../services/endpoints"
import { FetchError } from "../types/FetchError"

interface FormData {
  email: string
  wallet: string
  walletChain: string
  username: string
  password: string
  confirmPassword: string
}

interface FieldData {
  name: string
  updateFunction: (error: string) => void
}

interface FieldsDataMap {
  [key: string]: FieldData
}

const INITIAL_DATA: FormData = {
  email: "",
  wallet: "",
  walletChain: CHAINS[0],
  username: "",
  password: "",
  confirmPassword: "",
}

export default function Register() {
  const router = useRouter()
  const [data, updateFields] = useForm<FormData>(INITIAL_DATA)
  const [emailError, updateEmailError] = useState("")
  const [walletError, updateWalletError] = useState("")
  const [walletChainError, updateWalletChainError] = useState("")
  const [usernameError, updateUsernameError] = useState("")
  const [passwordError, updatePasswordError] = useState("")
  const [passwordRepeatError, updatePasswordRepeatError] = useState("")
  const { t } = useTranslation()

  const homeRedirect = async () => {
    try {
      await loginUser(data.email, data.password)
      router.push("/")
    } catch (errs: any) {
      Swal.fire({ title: t("login.signInError"), text: errs[0].message, icon: "error" })
    }
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      await createUser(data)
      await homeRedirect()
    } catch (errs: any) {
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
        title: t("register.error"),
        text:
          errs.length === 1
            ? t("errors.invalidField", { field: errorFields })
            : t("errors.invalidFields", { fields: errorFields }),
        icon: "error",
      })
    }
  }

  const FIELDS_DATA: FieldsDataMap = {
    email: {
      name: t("register.email"),
      updateFunction: (error: string) => updateEmailError(error),
    },
    walletAddress: {
      name: t("register.walletAddress"),
      updateFunction: (error: string) => updateWalletError(error),
    },
    walletChain: {
      name: t("register.walletChain"),
      updateFunction: (error: string) => updateWalletChainError(error),
    },
    username: {
      name: t("register.username"),
      updateFunction: (error: string) => updateUsernameError(error),
    },
    password: {
      name: t("register.password"),
      updateFunction: (error: string) => updatePasswordError(error),
    },
    passwordRepeat: {
      name: t("register.passwordRepeat"),
      updateFunction: (error: string) => updatePasswordRepeatError(error),
    },
  }

  return (
    <Layout>
      <div className="flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-md w-full space-y-8">
          <div>
            <img className="mx-auto h-12 w-auto" src={getResourceUrl("/logo.svg")} alt="" />
            <h2
              suppressHydrationWarning
              className="mt-6 text-center text-3xl font-extrabold text-gray-900"
            >
              {t("register.createAccount")}
            </h2>
            <p suppressHydrationWarning className="text-center pt-2">
              {t("register.createTo")}
            </p>
          </div>

          <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            <div className="rounded-md shadow-sm flex flex-col gap-2">
              <FormType
                name={t("register.email")}
                type="email"
                value={data.email}
                error={emailError}
                onChange={(e) => updateFields({ email: e.target.value })}
              />
              <FormType
                name={t("register.walletAddress")}
                value={data.wallet}
                error={walletError}
                onChange={(e) => updateFields({ wallet: e.target.value })}
              />
              <FormSelect
                name={t("register.walletChain")}
                options={CHAINS}
                value={data.walletChain}
                error={walletChainError}
                onChange={(e) => updateFields({ walletChain: e.target.value })}
              />
              <FormType
                name={t("register.username")}
                value={data.username}
                error={usernameError}
                onChange={(e) => updateFields({ username: e.target.value })}
              />
              <FormType
                name={t("register.password")}
                type="password"
                value={data.password}
                error={passwordError}
                onChange={(e) => updateFields({ password: e.target.value })}
              />
              <FormType
                name={t("register.passwordRepeat")}
                type="password"
                value={data.confirmPassword}
                error={passwordRepeatError}
                onChange={(e) => updateFields({ confirmPassword: e.target.value })}
              />
            </div>

            <div>
              <button
                suppressHydrationWarning
                type="submit"
                className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-bold rounded-md text-white bg-cyan-600 hover:bg-cyan-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-cyan-500"
              >
                {t("register.createAccount")}
              </button>
              <p suppressHydrationWarning className="text-center py-4">
                {t("register.haveAccount")}
                <Link href="/login" passHref>
                  <a suppressHydrationWarning className="text-cyan-600 pl-2">
                    {t("register.login")}
                  </a>
                </Link>
              </p>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  )
}
