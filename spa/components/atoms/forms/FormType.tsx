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
  error = "",
  value,
  onChange,
}: Props) {
  return (
    <FormInput name={name} error={error}>
      <input
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        required={isRequired}
        autoComplete="off"
        placeholder={placeholder}
        className={`pl-3 sm:text-sm rounded-lgfocus:ring-cyan-800 focus:border-cyan-800 text-cyan-700 placeholder:text-slate-400 shadow-sm
          ${error === "" ? "border-slate-300" : "border-red-600"}`}
      />
    </FormInput>
  )
}
