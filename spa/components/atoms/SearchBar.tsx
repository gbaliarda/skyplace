import { BaseSyntheticEvent, useEffect, useState } from "react"
import { useRouter } from "next/router"
import { MagnifyingGlassIcon } from "@heroicons/react/24/outline"
import { SearchType } from "../../types/Filters"

const SearchBar = () => {
  const router = useRouter()
  const querySearch = router.query.search
  const [searchFilter, setSearchFilter] = useState({
    search: "",
    searchFor: SearchType.Nft,
  })
  useEffect(() => {
    if (!router.isReady) return

    setSearchFilter({
      ...searchFilter,
      search: Array.isArray(querySearch) ? querySearch[0] : querySearch ?? "",
    })
  }, [router.isReady])

  const handleSubmit = (e: BaseSyntheticEvent) => {
    e.preventDefault()
    router.push(`explore?searchFor=${searchFilter.searchFor}&search=${searchFilter.search}`)
  }

  return (
    <div className="w-2/5 relative flex flex-row flex-start rounded border border-gray-300 text-cyan-800">
      <form
        onSubmit={handleSubmit}
        className="w-full flex m-0 focus-within:shadow-[0px_3px_20px_4px_rgba(0,0,0,0.05)]"
      >
        <select
          name="searchFor"
          className="border-0 border-r-[1px] pl-2 pr-7 border-gray-300 rounded-l cursor-pointer outline-none focus:outline-none"
          value={searchFilter.searchFor}
          onChange={(ev) =>
            setSearchFilter({
              ...searchFilter,
              searchFor: ev.currentTarget.value as SearchType,
            })
          }
        >
          {Object.keys(SearchType).map((type) => (
            <option key={type} value={type.toLowerCase()} className="text-slate-500">
              {type}
            </option>
          ))}
        </select>
        <input
          name="search"
          className="pl-2 outline-none w-full border-none p-0 focus:border-none focus:ring-0"
          type="text"
          placeholder=""
          value={searchFilter.search}
          onChange={(ev) => setSearchFilter({ ...searchFilter, search: ev.currentTarget.value })}
        />
        <button
          type="submit"
          className="border-l cursor-pointer border-gray-300 px-2 flex items-center"
        >
          <MagnifyingGlassIcon className="w-6 h-8 text-cyan-500" />
        </button>
      </form>
    </div>
  )
}

export default SearchBar
