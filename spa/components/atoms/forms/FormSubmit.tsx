export default function FormSubmit({
  value,
  classes,
  disabled,
}: {
  value: string
  classes: string
  disabled?: boolean
}) {
  return <input type="submit" value={value} disabled={disabled} className={classes} />
}
