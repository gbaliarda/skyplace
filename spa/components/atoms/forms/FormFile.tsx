import { forwardRef } from "react"
import FormInput from "./FormInput"

interface Props {
  name: string
  error?: string
}

const FormFile = forwardRef<HTMLInputElement, Props>(({ name, error }: Props, ref) => {
  return (
    <FormInput name={name} error={error}>
      <input
        type="file"
        ref={ref}
        required
        className="mt-3 block text-sm text-slate-500 file:mr-4 file:py-2 file:px-4
        file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-cyan-50 file:text-cyan-700 hover:file:bg-cyan-100"
      />
    </FormInput>
  )
})

export default FormFile
