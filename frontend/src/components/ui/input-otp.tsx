"use client"

import React from "react"
import OTPInput from "react-otp-input"
import { cn } from "@/lib/utils"

interface InputOTPProps extends React.ComponentProps<typeof OTPInput> {
  containerClassName?: string
  inputClassName?: string
}

const InputOTP = ({ containerClassName, inputClassName, ...props }: InputOTPProps) => (
  <OTPInput
    containerStyle={cn("flex items-center gap-2", containerClassName)}
    inputStyle={cn("w-10 h-10 text-center border rounded-md", inputClassName)}
    {...props}
  />
)

export { InputOTP }
