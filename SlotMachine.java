

import java.util.Arrays;

public class SlotMachine {
    
    public enum Symbol {
        BELLS("Bells", 10), FLOWERS("Flowers", 5), FRUITS("Fruits", 3),
        HEARTS("Hearts", 2), SPADES("Spades", 1);
        
        // symbol name
        private final String name;
        
        // payout factor (i.e. multiple of wager) when matching symbols of this type
        private final int payoutFactor;
        
        Symbol(String name, int payoutFactor) {
            this.name = name;
            this.payoutFactor = payoutFactor;
        }
        
        public String getName() {
            return name;
        }
        
        public int getPayoutFactor() {
            return payoutFactor;
        }
        
    }
    
    /**
     * Constructor
     * @param numReels number of reels in slot machine
     * @param odds odds for each symbol in a reel, indexed by its enum ordinal value; odds value is non-zero and sums to 1
     * @param wagerUnitValue unit value in cents of a wager
     */
    
    private int numReels;
    private double[] odds;
    private int wagerUnitValue;
    private long tPayout;
    private long tReceipts;
    
    public SlotMachine(int numReels, double [] odds, int wagerUnitValue) {
        this.numReels = numReels;
        this.odds = odds;
        this.wagerUnitValue = wagerUnitValue;
        this.tPayout = 0;
        this.tReceipts = 0;
    }
    
    /**
     * Get symbol for a reel when the user pulls slot machine lever
     * @return symbol type based on odds (use Math.random())
     */
    public Symbol getSymbolForAReel() {
        double num = Math.random();
        double sum = 0;
        for (int i = 0; i < odds.length; i++) {
        	if (num >= sum && num < sum + odds[i]) {
            	return Symbol.values()[i];
            }
            else{
            	sum = sum + odds[i];
            }                
        }
        return null;
    }
    
    
    /**
     * Calculate the payout for reel symbols based on the following rules:
     * 1. If more than half but not all of the reels have the same symbol then payout factor is same as payout factor of the symbol
     * 2. If all of the reels have the same symbol then payout factor is twice the payout factor of the symbol
     * 3. Otherwise payout factor is 0
     * Payout is then calculated as wagerValue multiplied by payout factor
     * @param reelSymbols array of symbols one for each reel
     * @param wagerValue value of wager given by the user
     * @return calculated payout
     */
    public long calcPayout(Symbol[] reelSymbols, int wagerValue) {
        Arrays.sort(reelSymbols);
        long payout = 0;
        
        if(reelSymbols[0] == reelSymbols[reelSymbols.length-1]) {
            payout = wagerValue * 2 * reelSymbols[0].getPayoutFactor();
            return payout;
        }
        
        int left = 0, right = 0;
        while(right < reelSymbols.length) {
            if(reelSymbols[left] == reelSymbols[right]) {
                right ++;
            	if(right - left > reelSymbols.length/2) {
            		payout = wagerValue * reelSymbols[left].getPayoutFactor();
            		return payout;
            	}
            }
            else{
            	left = right;            	
            }
        }
        return 0;
    }
    
    /**
     * Called when the user pulls the lever after putting wager tokens
     * 1. Get symbols for the reels using getSymbolForAReel()
     * 2. Calculate payout using calcPayout()
     * 3. Display the symbols, e.g. Bells Flowers Flowers..
     * 4. Display the payout in dollars and cents e.g. $2.50
     * 5. Keep track of total payout and total receipts from wagers
     * @param numWagerUnits number of wager units given by the user
     */
    public void pullLever(int numWagerUnits){
    	if (numWagerUnits != 0){
    		Symbol [] reelSymbols = new Symbol[numReels];
            for(int i = 0; i < reelSymbols.length; i++) {
                reelSymbols[i] = getSymbolForAReel();
                System.out.print(reelSymbols[i].getName() + " ");
            }
            long payout = calcPayout(reelSymbols, numWagerUnits * wagerUnitValue);
            System.out.println("\npayout = " + "$" + String.format("%.2f", (float)payout/100));
            tReceipts = tReceipts + numWagerUnits * wagerUnitValue;
            tPayout = tPayout + payout;
    	}
    }
    
    /**
     * Get total payout to the user as percent of total wager value
     * @return e.g. 85.5
     */
    public double getPayoutPercent() {
    	if(tPayout!=0 && tReceipts !=0){
    		return 100 * (float)tPayout/tReceipts;
    	}
    	else{
    		return 0;
    	}
    		
    }
    
    /**
     * Clear the total payout and wager value
     */
    public void reset() {
        tReceipts = 0;
        tPayout = 0;
    }
    
    public static void main(String [] args) {
        double [] odds = new double[Symbol.values().length];
        // sum of odds array values must equal 1.0
        odds[Symbol.HEARTS.ordinal()] = 0.3;
        odds[Symbol.SPADES.ordinal()] = 0.25;
        odds[Symbol.BELLS.ordinal()] = 0.05;
        odds[Symbol.FLOWERS.ordinal()] = 0.2;
        odds[Symbol.FRUITS.ordinal()] = 0.2;
        
        SlotMachine sm = new SlotMachine(3, odds, 25); // quarter slot machine
        sm.pullLever(2);
        sm.pullLever(1);
        sm.pullLever(3);
        System.out.println("Pay out percent to user = " + sm.getPayoutPercent());
        sm.reset();
        sm.pullLever(4);
        sm.pullLever(1);
        sm.pullLever(1);
        sm.pullLever(2);
        System.out.println("Pay out percent to user = " + sm.getPayoutPercent());
    }
    
}