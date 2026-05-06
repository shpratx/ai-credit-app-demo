import { useEffect, useRef } from 'react';
import { Button } from './ui/Button';

interface MilestoneModalProps {
  title: string;
  description: string;
  scoreStart: number;
  scoreCurrent: number;
  pointsGained: number;
  duration: string;
  onDismiss: () => void;
  onContinue: () => void;
}

export function MilestoneModal({
  title,
  description,
  scoreStart,
  scoreCurrent,
  pointsGained,
  duration,
  onDismiss,
  onContinue,
}: MilestoneModalProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    dialogRef.current?.showModal();
  }, []);

  return (
    <dialog
      ref={dialogRef}
      className="fixed inset-0 m-auto w-[90%] max-w-sm rounded-[20px] p-8 text-center shadow-xl backdrop:bg-black/40"
      aria-label={title}
      onClose={onDismiss}
    >
      <div className="text-5xl mb-3" aria-hidden="true">🎉</div>
      <h2 className="text-2xl font-bold text-lloyds-green">{title}</h2>
      <p className="text-sm text-text-secondary mt-3 leading-relaxed">{description}</p>
      <div className="mt-5 p-4 bg-lloyds-green-light rounded-lg">
        <p className="text-caption text-text-secondary">Your journey</p>
        <p className="text-xl font-bold mt-1">{scoreStart} → {scoreCurrent}</p>
        <p className="text-caption font-bold text-lloyds-green mt-1">+{pointsGained} points in {duration}</p>
      </div>
      <Button className="mt-6 w-full" onClick={onContinue} aria-label="Keep going">
        Keep going!
      </Button>
      <button
        className="mt-3 text-sm text-text-secondary underline cursor-pointer bg-transparent border-none"
        onClick={onDismiss}
        aria-label="Dismiss celebration"
      >
        Dismiss
      </button>
    </dialog>
  );
}
