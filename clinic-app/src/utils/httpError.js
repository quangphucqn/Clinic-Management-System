/**
 * @param {unknown} error
 * @returns {string}
 */
export function getErrorMessage(error) {
  if (error && typeof error === 'object' && 'response' in error) {
    /** @type {{ response?: { data?: { message?: string } } }} */
    const res = error
    const msg = res.response?.data?.message
    if (typeof msg === 'string' && msg.trim()) return msg
  }
  if (error instanceof Error && error.message) return error.message
  return 'Đã xảy ra lỗi. Vui lòng thử lại.'
}
