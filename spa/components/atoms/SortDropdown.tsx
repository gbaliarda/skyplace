import { useRef } from "react"

export type SortOption = {
  key: string
  value: string
}

interface Props {
  sortDefaultValue: string
  options: SortOption[]
  handleSortChange: (sort: string) => void
}

const SortDropdown = ({ sortDefaultValue, options, handleSortChange }: Props) => {
  const sortRef = useRef<HTMLSelectElement>(null)

  return (
    <div className="flex text-2xl pr-6">
      <select
        className="select w-full max-w-xs border-2 border-cyan-600 text-cyan-700 font-bold focus:outline-none"
        onChange={() => handleSortChange(sortRef.current?.value || "")}
        defaultValue={sortDefaultValue}
        ref={sortRef}
      >
        {options.map((option) => (
          <option value={option.key} key={option.key} className="bg-gray-100 flex w-full text-left">
            {option.value}
          </option>
        ))}
      </select>
    </div>
  )
}

export default SortDropdown
