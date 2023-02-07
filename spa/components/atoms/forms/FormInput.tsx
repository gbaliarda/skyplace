import { ChangeEvent, ReactNode } from "react"

export interface CommonInputProps {
  name: string
  value: string | number
  onChange: (e: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => void
  error?: string
}

interface Props {
  name: string
  error?: string
  children: ReactNode
}

export default function FormInput({ name, error = "", children }: Props) {
  return (
    <label className="flex flex-col gap-1">
      <span suppressHydrationWarning className="text-slate-600">
        {name}
      </span>
      {children}
      {error && (
        <p suppressHydrationWarning className="text-red-500">
          {error}
        </p>
      )}
    </label>
  )
}
