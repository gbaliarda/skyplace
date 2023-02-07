import FormInput, { CommonInputProps } from "./FormInput"

interface Props extends CommonInputProps {
  placeholder?: string
  type?: string
  isRequired?: boolean
}

export default function FormType({
  placeholder = "",
  type = "text",
  isRequired = true,
  name,
  error,
  value,
  onChange,
}: Props) {
  return (
    <FormInput name={name} error={error}>
      <input
        type={type}
        value={value}
        onChange={onChange}
        required={isRequired}
        autoComplete="off"
        placeholder={placeholder}
        className="pl-3 sm:text-sm rounded-lg border-slate-300 focus:ring-cyan-800 focus:border-cyan-800 text-cyan-700 placeholder:text-slate-400 shadow-sm"
      />
    </FormInput>
  )
}
