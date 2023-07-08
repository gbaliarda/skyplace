import { forwardRef } from "react"
import FormInput from "./FormInput"

interface Props {
  name: string
  error?: string
  dataTestId?: string
}

const FormFile = forwardRef<HTMLInputElement, Props>(({ name, error, dataTestId }: Props, ref) => {
  return (
    <FormInput name={name} error={error}>
      <input
        type="file"
        accept="image/*"
        ref={ref}
        data-testid={dataTestId}
        required
        className={`mt-3 block text-sm text-slate-500 file:mr-4 file:py-2 file:px-4
        file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-cyan-50 file:text-cyan-700 hover:file:bg-cyan-100
        ${error === "" ? "border-slate-300" : "border-red-600"}`}
      />
    </FormInput>
  )
})

export default FormFile
