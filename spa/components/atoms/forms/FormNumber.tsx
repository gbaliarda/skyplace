import FormInput, { CommonInputProps } from "./FormInput"

interface Props extends CommonInputProps {
  decimals?: number
}

export default function FormNumber({ name, decimals = 0, error, value, onChange }: Props) {
  return (
    <FormInput name={name} error={error}>
      <input
        type="number"
        value={value}
        onChange={onChange}
        autoComplete="off"
        required
        placeholder="0"
        min="0"
        step={decimals ? `0.${"0".repeat(decimals - 1)}1` : "1"}
        className={`pl-3 sm:text-sm rounded-lg focus:ring-cyan-800 focus:border-cyan-800 text-cyan-700 placeholder:text-slate-400 shadow-sm
        ${error === "" ? "border-slate-300" : "border-red-600"}`}
      />
    </FormInput>
  )
}
