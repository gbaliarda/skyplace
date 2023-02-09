import Link from "next/link"
import { useTranslation } from "next-export-i18n"
import Layout from "../components/Layout"
import { getResourceUrl } from '../services/endpoints';

const Home = () => {
  const { t } = useTranslation()

  return (
    <Layout>
      <div className="h-full flex flex-col max-w-[100vw]">
        <div className="container px-4 py-8 flex flex-col-reverse lg:flex-row gap-4 lg:gap-12 grow justify-center items-center overflow-hidden md:overflow-visible">
          {/* Message and buttons */}
          <div className="flex flex-1 flex-col items-center lg:items-start lg:mt-[-7rem]">
            <h2
              suppressHydrationWarning
              className="text-3xl mid:text-4 lg:text-5xl text-center lg:text-left mb-6 font-semibold pt-12"
            >
              {t("index.discover")}
            </h2>
            <p
              suppressHydrationWarning
              className="text-bookmark-grey text-lg text-center lg:text-left mb-6"
            >
              {t("index.become")}.
            </p>
            <div className="flex justify-center flex-wrap gap-6">
              <Link href="/create">
                <button
                  suppressHydrationWarning
                  type="button"
                  className="shadow-md px-6 rounded-md transition duration-300 bg-cyan-600 hover:bg-cyan-800 text-white hover:shadow-xl h-12"
                >
                  {t("index.create")}
                </button>
              </Link>
              <Link href="/explore">
                <button
                  suppressHydrationWarning
                  type="button"
                  className="shadow-md px-6 rounded-md transition duration-300 border-2 border-cyan-600 hover:border-cyan-800 hover:bg-cyan-800 text-cyan-600 hover:text-white hover:shadow-xl h-12"
                >
                  {t("index.explore")}
                </button>
              </Link>
            </div>
          </div>
          {/* Image and circles */}
          <div className="relative flex flex-items-center justify-center flex-1 md:mb-16 lg:mb-0 w-full max-w-lg lg:mt-[-7rem]">
            <div className="absolute -top-4 -left-8 w-80 h-80 bg-cyan-300 rounded-full mix-blend-multiply filter blur-xl opacity-75 animate-blob z-0" />
            <div className="absolute -top-4 -right-8 w-80 h-80 bg-yellow-300 rounded-full mix-blend-multiply filter blur-xl opacity-75 animate-blob delay-2000 z-0" />
            <div className="absolute top-8 w-80 h-80 bg-red-300 rounded-full mix-blend-multiply filter blur-xl opacity-75 animate-blob delay-4000 z-0" />
            <img className="z-10 h-80 w-68" src={getResourceUrl("/index/monkeyNft.jpg")} alt="" />
          </div>
        </div>
      </div>
    </Layout>
  )
}

export default Home
