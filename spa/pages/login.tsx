import { ChangeEvent, FormEvent, useState } from "react"
import Link from "next/link"
import { LockClosedIcon } from "@heroicons/react/24/solid"
import { useRouter } from "next/router"
import { useTranslation } from "next-export-i18n"
import Swal from "sweetalert2"
import Layout from "../components/Layout"
import useForm from "../hooks/useForm"
import { loginUser } from "../services/users"
import { getResourceUrl } from "../services/endpoints"
import Spinner from "../components/atoms/Spinner"

interface FormData {
  email: string
  password: string
  rememberMe: boolean
}

const INITIAL_DATA: FormData = {
  email: "",
  password: "",
  rememberMe: false,
}

export default function Login() {
  const router = useRouter()
  const [data, updateFields] = useForm<FormData>(INITIAL_DATA)
  const [loggingIn, setLoggingIn] = useState(false)
  const { t } = useTranslation()
  const { from } = router.query as { from: string | undefined }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    try {
      setLoggingIn(true)
      await loginUser(data.email, data.password, data.rememberMe)
      router.replace(from === undefined ? "/" : from)
    } catch (errs: any) {
      const description = errs[0] ? errs[0].cause.description : ""
      Swal.fire({ title: t("login.signInError"), text: description, icon: "error" })
    } finally {
      setLoggingIn(false)
    }
  }

  return (
    <Layout>
      <div className="flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-md w-full space-y-8">
          <img className="mx-auto h-12 w-auto" src={getResourceUrl("/logo.svg")} alt="" />
          <h2
            suppressHydrationWarning
            className="mt-6 text-center text-3xl font-extrabold text-gray-900"
          >
            {t("login.signIn")}
          </h2>
          <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            <div className="rounded-md shadow-sm -space-y-px">
              <LoginInput
                type="email"
                value={data.email}
                onChange={(e) => updateFields({ email: e.target.value })}
                placeholder={t("login.email")}
              />
              <LoginInput
                type="password"
                value={data.password}
                onChange={(e) => updateFields({ password: e.target.value })}
                className="rounded-t-none rounded-b-md"
                placeholder={t("login.password")}
              />
            </div>

            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <input
                  type="checkbox"
                  checked={data.rememberMe}
                  onChange={(e) => updateFields({ rememberMe: e.target.checked })}
                  className="h-4 w-4 text-cyan-600 focus:ring-cyan-500 border-gray-300 rounded"
                />
                <label suppressHydrationWarning className="ml-2 block text-sm text-gray-900">
                  {t("login.rememberMe")}
                </label>
              </div>
              <Link href={{ pathname: "/register", query: { from } }} passHref>
                <a
                  suppressHydrationWarning
                  className="font-medium text-sm text-cyan-600 hover:text-cyan-500"
                >
                  {t("login.noAccount")}
                </a>
              </Link>
            </div>

            {!loggingIn ? (
              <button
                suppressHydrationWarning
                type="submit"
                className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-md font-medium rounded-md text-white bg-cyan-600 hover:bg-cyan-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-cyan-500"
              >
                <span className="absolute left-0 inset-y-0 flex items-center pl-3">
                  <LockClosedIcon className="h-5 w-5 text-cyan-500 group-hover:text-cyan-400" />
                </span>
                {t("login.signInButton")}
              </button>
            ) : (
              <button
                type="button"
                data-testid="spinner"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md bg-cyan-700 cursor-wait"
              >
                <Spinner />
              </button>
            )}
          </form>
        </div>
      </div>
    </Layout>
  )
}

interface LoginInputProps {
  type: string
  placeholder: string
  value: string
  onChange: (e: ChangeEvent<HTMLInputElement>) => void
  className?: string
}

const LoginInput = ({ type, placeholder, value, onChange, className }: LoginInputProps) => (
  <div>
    <label className="sr-only">{placeholder}</label>
    <input
      type={type}
      value={value}
      onChange={onChange}
      required
      className={`appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-cyan-500 focus:border-cyan-500 focus:z-10 sm:text-sm ${className}`}
      placeholder={placeholder}
      suppressHydrationWarning
    />
  </div>
)
