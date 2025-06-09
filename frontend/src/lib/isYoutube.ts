// lib/isYoutube.ts
export const isYoutube = (url?: string): boolean => {
  if (!url) return false;
  return url.includes("youtube.com") || url.includes("youtu.be");
};
