import FormInput, { CommonInputProps } from "./FormInput"

interface Props extends CommonInputProps {
  classes: string
  placeholder?: string
}

export default function FormTextArea({
  classes,
  placeholder = "",
  name,
  error,
  value,
  onChange,
}: Props) {
  return (
    <FormInput name={name} error={error}>
      <textarea
        value={value}
        onChange={onChange}
        autoComplete="off"
        placeholder={placeholder}
        className={classes}
      />
    </FormInput>
  )
}
