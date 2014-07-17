//	This file is part of ZebraViews.
//
//	Copyright 2014 AnimatedJuzz <kazasrinivas3@gmail.com>
//
//	ZebraViews is free software: you can redistribute it and/or modify
//	under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.
//
//	ZebraViews is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//
//	You should have received a copy of the GNU General Public License
//	along with ZebraViews.  If not, see <http://www.gnu.org/licenses/>.

package zebradev.zebraviews.processor;

import zebradev.zebraviews.common.Requests;

import com.esotericsoftware.minlog.Log;

public abstract class Processor implements Runnable {
	
	private Product product;
	public Requests failed;
	
	@Override
	public void run() {
		if (this.product != null)
		{
			try {
				this.onExecute(this.product);
			} catch (ProcessingException e) {
				Log.error("Processor " + e.getProcessor() + " failed", e);
				this.failed = e.getEssentialFailed();
			}
		}
		else
		{
			Log.error("Product object not supplied!");
			this.failed = Requests.ESSENTIAL_BOTH;
		}
	}
	
	public Product getProduct() {
		return this.product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}
	
	protected abstract void onExecute(Product product) throws ProcessingException;
}
