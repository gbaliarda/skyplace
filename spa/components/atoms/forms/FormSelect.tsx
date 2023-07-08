import FormInput, { CommonInputProps } from "./FormInput"

interface Props extends CommonInputProps {
  options: string[]
  translations?: string[]
}

export default function FormSelect({ options, name, value, onChange, error, translations }: Props) {
  return (
    <FormInput name={name} error={error}>
      <select
        required
        name={name}
        value={value}
        onChange={onChange}
        className={`pl-3 sm:text-sm rounded-lg focus:ring-cyan-800 focus:border-cyan-800 text-cyan-700 placeholder:text-slate-400 shadow-sm
        ${error === "" ? "border-slate-300" : "border-red-600"}`}
      >
        {options.map((category, i) => (
          <option key={category} value={category}>
            {translations ? translations[i] : category}
          </option>
        ))}
      </select>
    </FormInput>
  )
}
