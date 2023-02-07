import { useState } from "react"

export default function useForm<FormData>(initialData: FormData) {
  const [data, setData] = useState(initialData)

  const updateFields = (fields: Partial<FormData>) => {
    setData((prev) => ({ ...prev, ...fields }))
  }

  return [data, updateFields] as const
}
